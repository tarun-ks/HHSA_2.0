<%@page import="com.nyc.hhs.util.HHSPortalUtil"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant, com.tangosol.net.NamedCache, com.tangosol.net.CacheFactory, java.io.PrintWriter"%>
<%@ page errorPage="/error/errorpage.jsp" %>

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
	        	<li>&nbsp;</li>
	        <li>5. <a class="link" title="Administrator Settings - Screen Locking Details" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_40&_nfls=false&removeNavigator=true&navigatefrom=landing&printCache=true">Administrator Settings - Screen Locking Details</a></li>
	   		<%
	   			if(null != HHSPortalUtil.parseQueryString(request,"isAdminUser") && HHSPortalUtil.parseQueryString(request,"isAdminUser").equalsIgnoreCase("hhsAdmin"))
	   			{
	   		%>
	   			<li>&nbsp;</li>
	        <li>6. <a class="link" title="ReCache" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_40&_nfls=false&removeNavigator=true&navigatefrom=landing&reCahce=true">ReCache</a></li>
	        	<li>&nbsp;</li>
	        <li>7. <a class="link" title="Reinitialize Log4j" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_40&_nfls=false&removeNavigator=true&navigatefrom=landing&reinitializeLog4j=true">Reinitialize Log4j</a></li>
	        	<li>&nbsp;</li>
	        <li>8. <a class="link" title="Release All Locks" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_40&_nfls=false&removeNavigator=true&navigatefrom=landing&cleanCache=true">Release All Locks</a></li>
	        	<li>&nbsp;</li>
	        <li>9. <a class="link" title="Update Cache" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_40&_nfls=false&removeNavigator=true&navigatefrom=landing&updateCache=coherence">Update Cache</a></li>
	   		<%
	   			}
	   		%>
	    </ul>
	    <!-- hidden value to pass parameter from jsp to java program -->      
	    <input type="hidden" name="submitLinkValue" value="" id="linkValue"></input>
	    <input type="hidden" name="submitValue"></input>  
		<ul>
			</ul>
		</ul>
  	<%}else{ %>
 		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
 	<%}%>
</form>
