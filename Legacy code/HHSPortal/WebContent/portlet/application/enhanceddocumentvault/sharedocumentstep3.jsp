<%@page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>
<portlet:resourceURL var="fromStep2Sharing" id="fromStep2Sharing" escapeXml="false"></portlet:resourceURL>
<portlet:resourceURL var="backtoStep1Sharing" id="backtoStep1Sharing" escapeXml="false"></portlet:resourceURL>
<script type="text/javascript" src="../js/sharedocuments.js"></script>
<script type="text/javascript">
var shareAction3;
//on load function to perform various checks on loading of jsp
function onReady(){ 
		shareAction3 = $("#share3").attr('action');
		splitPreviousScreenValues("<%=request.getAttribute("proNameString")%>");
		populateNYCAgency('<%=request.getAttribute("agencySet")%>');
}
// This will execute to populate table with NyC Agency selected
function populateTable() {
	var selecttype = document.getElementById('selectagency');
	var agencyName = selecttype.options[selecttype.selectedIndex].text;
	if ("All NYC Agencies" == agencyName) {
		populateAllNYCAgencies('<%=request.getAttribute("agencySet")%>');
	} else {
		insertRowInTable(agencyName, "^AGENCY");
		applyCssToTable("#mytable2 tr");
	}
}
</script>
<!-- Sharing Overlay Step 3 -->
<form id="share3" action="" method ="post" name="share3">
	<input type="hidden" name="proNameString" id="proNameString">
	<input type="hidden" id="fromStep2SharingForm" value="${fromStep2Sharing}"/>
	<input type="hidden" id="backtoStep1SharingForm" value="${backtoStep1Sharing}"/>
		<!--Start of R4 Document Vault changes: Component Mapping check added for Agency security matrix-->
		<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S041_PAGE, request.getSession()) || CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession()) ) {%>
		<!--End of R4 Document Vault changes -->
			<div class="wizardTabs" style='width:800px;'>
				<div style='padding-left:10px'>
					<p>If you would like to grant an NYC Agency access to your documents, select them from the drop-down and 
					Click the "Add Agency" button. Once complete, Click 
					the "Next" button.</p>
					<br/>
					<p>If you do not want to grant any NYC Agency access to your documents, click the "Next" button now.</p>
					<div class='hr'></div>
					<div class="formcontainer">
						<select id="selectagency" class='selectagency-align'>
						</select>
						<input type="button" value="+ Add Agency" title="+ Add Agency" class="button selectagency-align" name="addProvider" onclick="populateTable()"/>
						
						<div class="tabularWrapper"  style="height: 250px !important; overflow: auto;">
						<b>Granting access to the following:</b>
						
						<table border="1" id='mytable2'>
						</table>
							
							
							
						</div>
						<div class="buttonholder">
							<input type="button" id="cancelshare3" value="Cancel" title="Cancel" class="graybtutton"/>
							<input type="button" id="backshare2" value="Back" title="Back" class="graybtutton"/>
							<input type="button" value="Next" title="Next" id="nextshare3"/>
						
						</div>
				 </div>
			 </div>
			 </div>
		 <%}else{ %>
		 	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		<%} %>
</form>
