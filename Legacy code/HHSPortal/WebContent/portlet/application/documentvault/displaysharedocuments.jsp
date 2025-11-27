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
%>


<script type="text/javascript">
var displayShareAction = document.displaysharedform.action;
//on load function to perform various checks on loading of jsp
function onReady(){
	// This will execite when RemoveAll button is clicked
	$(".alert-box-removeselectedprovs").find('#removeall').click(function() { // bind click event to link
				removeallprov('<%=request.getAttribute("documentId")%>','<%=request.getAttribute("documentName")%>');
		    	var options = 
		    	{
			    	success: function(responseText, statusText, xhr ) 
					{
					response = new String(responseText);
					var responses = response.split("|");
					if(responses[1] == "Error")
					{					
						$( "#formcontainer1" ).show();
						
				}else{
						$(".overlay").closeOverlay();
						submitSuccessForRemovedDocs(responses[3],responses[4]);
				}},
				error:function (xhr, ajaxOptions, thrownError)
				{   
					showErrorMessagePopup();
					removePageGreyOut();
				}};
		    	$(this.form).ajaxSubmit(options);
				return false;
	});
	// This will execite when Remove Selected button is clicked
	$(".alert-box-removeselectedprovs").find('#removeselected').click(function() { // bind click event to link
				removeselectedprov('<%=request.getAttribute("documentId")%>','<%=request.getAttribute("documentName")%>');
		    	var options = 
		    	{
			    	success: function(responseText, statusText, xhr ) 
					{
						response = new String(responseText);
						var responses = response.split("|");
						if(responses[1] == "Success")
						{					
									$(".overlay").closeOverlay();
									submitSuccessForRemovedDocs(responses[3],responses[4]);
									//$.unblockUI();
						}
				},
					error:function (xhr, ajaxOptions, thrownError)
					{   
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
<div class="overlaycontent">
	<form id="displaysharedform" name="displaysharedform" action="<portlet:actionURL/>" method ="post">
		<input type="hidden" id="message" name ="message">
		<input type="hidden" id="messageType" name ="messageType">
		<input type="hidden" id="nextDisplaySharedAction" name ="next_action">
			<!--Start of R4 Document Vault changes: Component Mapping check added for Agency security matrix-->
			<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S038_PAGE, request.getSession())|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())) {%>
			<!--End of R4 Document Vault changes -->
				<p><b>Your organization has chosen to share this file with the following Providers/NYC Government Agencies:</b></p>
				<div class="tabularWrapper" style='overflow:auto; height:270px !important';>
					<c:forEach var="organization" items="${providerArray}">
						<div>
							<span>
							<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S038_SECTION, request.getSession())){ %>
							<input type="checkbox" name="provCheck" id ="provCheck" value="${organization}" onclick="javascript:enableButton()"/>
							<%}
							else{%>
							<input type="checkbox" name="provCheck" id ="provCheck" value="${organization}" disabled="disabled"/>
							<%}%>
							
							</span>
							<span>${organization}</span>
						</div>
					</c:forEach> 
				</div> 
				<div class='clear'>&nbsp;</div>
				<div>
					<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S038_SECTION, request.getSession())){ %>
							<input class="button redbtutton" type="button" title="- Remove All" value="- Remove All" id="removeall"/>
					<%}
					else{%>
					<input class="button redbtutton" type="button" title="- Remove All" value="- Remove All" id="removeall" disabled="disabled"/>
					<%}%>
					<input class="button graybtutton" type="button" title="- Remove Selected" id="removeselected" value="- Remove Selected" disabled="disabled"/>
					<input type="button" value="Close" title="Close" id="closeoverlay" class="button graybtutton">
				</div>
			<%}else{ %>
				 <h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
			<%} %>
	</form>
</div>