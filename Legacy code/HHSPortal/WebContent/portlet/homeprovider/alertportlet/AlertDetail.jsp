<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page import="com.nyc.hhs.model.AlertInboxBean"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<portlet:defineObjects/>
<style type="text/css">
label.remove, .alertWrapper a{
	text-decoration: underline;
	color: #5077AA;
	padding-left: 20px;
	cursor:pointer
}
.linkReturnVault{
	margin: 0 8px 0 0
}
/* Updated In r6 for return payment.Margin-top extra character removed  */
.iconQuestion{
	margin-top: 0;
	margin-top:15px;
	*margin-top:0;
}
.alertWrapper a {
	padding-left: 0
}
</style>
<script type="text/javascript">
	/*
	* Added for R4: A check to change hyperlink of Alert in casse if user is not authorized to view this alert. 
	* Updated URL will redirect user to homepage with Error message.	
	*/
	pageGreyOut();
	var contextPathVariable = "<%=request.getContextPath()%>";
	$(document).ready(function() {
		//Start : R5 Added
		UpdateAlertInbox();
		//End : R5 Added
		var alertPermissionError = '${alertPermissionError}';
	if(null!=alertPermissionError && alertPermissionError !="" && typeof(alertPermissionError)!="undefined" ){
		$("#alertWrapperDiv>a").each(function() {
			var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_nfls=false?&_pageLabel=portlet_hhsweb_portal_page_provider_home&app_menu_name=home_icon&alertPermissionError=true";
			$(this).attr('href',url);
		});
	}
	removePageGreyOut();
	});

	function deleteAlertRow(Id)
	{	
		document.alertDetailform.action = document.alertDetailform.action + '&next_action=delete&notificationId='+Id; 	
		document.alertDetailform.submit();
	}
	function returntoInbox()
	{
		document.alertDetailform.action = document.alertDetailform.action + '&next_action=showpage';	
		document.alertDetailform.submit();
	}
</script>

<form name="alertDetailform" id="alertDetailform" action="<portlet:actionURL/>"  method ="post">
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_2, request.getSession())) {%>
	<div class="clear pad10">
		<span class="floatLft">
			<label class="remove" onclick="deleteAlertRow(${alertInboxBean.msNotificationId});" title="Delete Alert">
				Delete Alert
			</label>
		</span>
		
	 	<span class="floatRht ">
	 		<label class="iconQuestion">
				<a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Alerts/Notifications');"></a>
			</label>
			
	 		<label class='linkReturnVault'>
	 			<a href="#" title="Return to Alert Inbox" onclick="returntoInbox();">Return to Alert Inbox</a>
	 		</label>	
	 		
	 	</span>
		
	</div>
	 <div class="overlay"></div>
		<div class="alert-box-help">
			<div class="content">
	             <div id="newTabs" class='wizardTabs'>
	             	<div class="tabularCustomHead">Alerts/Notifications - Help Documents</div>
	             	<div id="helpPageDiv"></div>
	             </div>
			</div>
	       	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
		</div>
		<div class="alert-box-contact">
			<div class="content">
				<div id="newTabs">
					<div id="contactDiv"></div>
				</div>
			</div>
		</div>
	<p class='clear'></p>
	 
	
	<div class="tabularCustomHead">
		<span>Alert Information</span> 
		<span class="positionInner" style="color:#fff; right:10px;">Date:  ${alertInboxBean.msNotificationDate}</span>
	</div>
	<div class="tabularContainer"> 
			<div>
				<strong>Alert Name:</strong>
				<div>${alertInboxBean.msNotificationName}</div>
			</div>
				    
			<div class="pad10">
				<strong>Alert Details:</strong>
				<div id="alertWrapperDiv" class='alertWrapper'>${alertInboxBean.msNotificationDesc}</div>
			</div>			    			  
				    
		</div>

	<%}else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</form>

