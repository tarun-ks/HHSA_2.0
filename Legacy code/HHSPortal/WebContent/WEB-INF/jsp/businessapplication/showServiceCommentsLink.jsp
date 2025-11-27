<!--This jsp will have the comments for corresponding sections.-->
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<style type="text/css"> 
.tabularWrapper{
 	height: 300px;
 }
.ui-widget{
 	font-family: Verdana !important;
 	font-size: 12px !important;
}
#newTabs h5{
 	background:#E4E4E4;
 	color: #5077AA;
    font-size: 13px;
    font-weight: bold;
    padding: 6px 0px 6px 6px;
}
#newTabs #main-wrapper {width:100%;}
#newTabs #main-wrapper .bodycontainer {width:100%; background:#fff;}
</style>

<script type="text/javascript"> 
	$(document).ready(function() {
			$('#overlayLink').click(function() {    
				 $("#displayshared").append($(".commentHidden").show().remove());
				 $(".overlay").launchOverlay($(".alert-box-comments"), $("a.exit-panel"), "400px");
				 return false;		
			});	
	});
</script>
<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S116_7_8_9_PAGE, request.getSession())){%>
  	<a href="#" class="linkclass" name="overlayLink" id="overlayLink" title="Comments"><u>Show Comments</u></a>
		<div class="overlay"></div>
		<div class="alert-box alert-box-comments">
			<div class="content">
		  		<div id="newTabs">
					<div class="tabularCustomHead">Service Comments</div>
		            <div id="displayshared"></div>
				</div>
		  	</div>
		  	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
		</div>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>




