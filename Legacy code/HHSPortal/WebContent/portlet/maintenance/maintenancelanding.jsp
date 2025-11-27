<%@page import="com.nyc.hhs.util.CommonUtil"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page import="com.nyc.hhs.util.HHSPortalUtil"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%-- Start Release 3.6.0 Enhancement id 6516--%>
<portlet:defineObjects />
<form name="form" id="form">
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.FQ_S129_PAGE, request.getSession())) {%>
	    <h2>Maintenance Main Page</h2>
		<div class="hr"></div>
	  	<ul>
	    	<li>1. <a class="link" title="Taxonomy Maintenance" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintaintaxonomy&_nfls=true&removeNavigator=false&navigatefrom=landing">Taxonomy Maintenance</a></li>
	    	<li>&nbsp;</li>
	    	<ul>
	    	    <li style="padding-left:15px;">1.1 <a class="link" title="Taxonomy Tagging - Proposals/Contracts" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_taxonomytagging&_nfls=true&removeNavigator=false&navigatefrom=taxonomytaggingpage&reset=true">Taxonomy Tagging - Proposals/Contracts</a></li>
	    	</ul>    
	        <li>&nbsp;</li>
	        <li>2. <a class="link" title="FAQ Maintenance" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenancemanage&_nfls=false&removeNavigator=true&navigatefrom=landing">FAQ Maintenance</a></li>
	       	<li>&nbsp;</li>
	        <li>3. <a class="link" title="Agency Settings" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceagencysettings&_nfls=false&removeNavigator=true&navigatefrom=landing">Agency Settings</a></li>
	        <li>&nbsp;</li>
	        <li>4. <a class="link" title="Provider Settings - Multiple Account Access Requests" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceprovidersettings&_nfls=false&removeNavigator=true&navigatefrom=landing">Provider Settings - Multiple Account Access Request</a></li>
	        <%if(CommonUtil.getConditionalRoleDisplay(HHSComponentMappingConstant.ADMIN_SETTINGS, request.getSession())) {%>	
	       	<li>&nbsp;</li>
	        <li>5. <a class="link" title="Screen Lock Maintenance" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_admin_settings_portlet&_nfls=false&removeNavigator=true&navigatefrom=landing&printCache=true">Screen Lock Maintenance</a></li>
	   		<%}%>
		 <li>&nbsp;</li>
			<li>6. <a class="link" title="Program Name Maintenance" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceprogramnames&_nfls=false&removeNavigator=true&navigatefrom=landing">Program Name Maintenance</a></li>
		<%-- R7 start :Modification Auto Approval Enhancement--%>
		<li>&nbsp;</li>
			<li>7. <a class="link" title="Auto-Approval Maintenance" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceagencysettings&_nfls=false&removeNavigator=true&navigatefrom=landing&auto_approval=true">Auto-Approval Maintenance</a></li>
	    <%-- R7 end :Modification Auto Approval Enhancement--%>
	     <li>&nbsp;</li>
	     <%--End  QC 9401 R8.5.0 start : RFP Proposal Report Enhancement--%>   
			<li>8. <a class="link" title="RFP Proposal Status Report" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenancerfpreport&_nfls=false&removeNavigator=true&navigatefrom=landing">RFP Proposal Status Report</a></li>
	     <li>&nbsp;</li>
	     	<%
	     	String actionMenu = CommonUtil.getActionMenuAvailable(); 
	     	if( actionMenu != null && actionMenu.equalsIgnoreCase("true")   ) {%>
			<li>9. <a class="link" title="Action Menu" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceactionmenu&_nfls=false&removeNavigator=true&navigatefrom=landing">Action Menu Control</a></li>
            <%}%>
			
	    <%--End  QC 9401 R8.5.0 start : RFP Proposal Report Enhancement--%>
	    
	    </ul>
	    <!-- hidden value to pass parameter from jsp to java program -->      
	    <input type="hidden" name="submitLinkValue" value="" id="linkValue"></input>
	    <input type="hidden" name="submitValue"></input>  
		<ul>
			</ul>
		</ul>
		<%--End Release 3.6.0 Enhancement id 6516--%>
  	<%}else{ %>
 		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
 	<%}%>
</form>
