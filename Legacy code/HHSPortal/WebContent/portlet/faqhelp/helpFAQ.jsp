<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<style type="text/css">
.alert-box-contact{
	margin-left: 8%;
	top: 25%
}
</style>
<portlet:defineObjects />
<%String lsPublishStatus = null;
	if(null!=renderRequest.getPortletSession().getAttribute("publish", portletSession.APPLICATION_SCOPE)){
		lsPublishStatus =(String)renderRequest.getPortletSession().getAttribute("publish", portletSession.APPLICATION_SCOPE);
	    %>
	 	<script type="text/javascript">
			if(!window.opener.closed){
				window.opener.location.reload(true);
			}
			setTimeout("window.close()",1);
		</script>
<%} %> 
   
<%   
    String userInSession = ApplicationConstants.PROVIDER_ORG;
    if(request.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE)!=null){
       userInSession = (String)request.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
    }
    String lsNavigation = null;
	if (renderRequest.getAttribute("previewPage") != null) {
		lsNavigation = (String)renderRequest.getAttribute("previewPage");
	}
 
	String lsFilePath = "";
	String lsClassNameForTab1="";
	String lsClassNameForTab2="";
	String lsClassNameForTab3="";
	String lsClassNameForTab4="";
	String lsClassNameForTab5 = "";
	String classSe = "class='selected'";
	String userType = null;
	String pageTodisplay = null;
	if (renderRequest.getAttribute("userType") != null) {
		userType = (String)renderRequest.getAttribute("userType");
	}
	if (renderRequest.getAttribute("previewPage") != null) {
		pageTodisplay = (String)renderRequest.getAttribute("previewPage");
	}
	if (renderRequest.getAttribute("IncludeFAQ") != null) {
		lsFilePath = (String) renderRequest.getAttribute("IncludeFAQ");
		lsClassNameForTab1 = "class='selected'";
	} else {
		lsClassNameForTab1 = "";
		lsClassNameForTab2 = "";
		lsClassNameForTab3 = "";
		lsClassNameForTab4 = "";
		lsClassNameForTab5 = "";
	}
	
	if (renderRequest.getAttribute("IncludeHelp") != null) {
		lsFilePath = (String) renderRequest.getAttribute("IncludeHelp");
		lsClassNameForTab3 = "class='selected'";
	}else if (renderRequest.getAttribute("IncludeSampleHelp") != null) {
		lsFilePath = (String) renderRequest.getAttribute("IncludeSampleHelp");
		lsClassNameForTab4 = "class='selected'";
	}else if (renderRequest.getAttribute("IncludeAgencyHelp") != null) {
		lsFilePath = (String) renderRequest.getAttribute("IncludeAgencyHelp");
		lsClassNameForTab5 = "class='selected'";
	} else {
		lsClassNameForTab1 = "";
		lsClassNameForTab2 = "";
		lsClassNameForTab3 = "";
		lsClassNameForTab4 = "";
		lsClassNameForTab5 = "";
	}
	if (null != userType) {
		if("provider".equalsIgnoreCase(userType)){
			lsClassNameForTab1 = "class='selected'";
			lsClassNameForTab2 = "";
		}else {
			lsClassNameForTab2 = "class='selected'";
		    lsClassNameForTab1 = "";
		}
	} 
%>

<script type="text/javascript">
	var userType = '<%=userType%>';
</script>


<span><a name="top"></a></span>
	<h2>HHS Accelerator Help</h2> 
	<div class="clear"></div> 
	<div class="customtabs">
		<ul class='normalCustomTab'>
			<%if(pageTodisplay==null){ %>
			  	<% if(ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(userInSession)|| ApplicationConstants.CITY_ORG.equalsIgnoreCase(userInSession) ){%>
					<li <%=lsClassNameForTab1%>><a class="<%=lsClassNameForTab1%>" title="Provider FAQs" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_faq&_nfls=false&removeNavigator=true&action=FAQ&userType=provider">Provider FAQs</a></li>
					<li <%=lsClassNameForTab3%>><a class="<%=lsClassNameForTab3%>"  title="Provider Help Documents" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_faq&_nfls=false&removeNavigator=true&action=helpdocuments">Provider Help Documents</a></li>
					<li <%=lsClassNameForTab4%>><a class="<%=lsClassNameForTab4%>"  title="Provider Sample Documents" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_faq&_nfls=false&removeNavigator=true&action=sampledocuments">Provider Sample Documents</a></li>
				<%} %>
				<%  if(ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(userInSession)|| ApplicationConstants.CITY_ORG.equalsIgnoreCase(userInSession) ){%>
					<li <%=lsClassNameForTab2%>><a class="<%=lsClassNameForTab2%>" title="Agency FAQs" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_faq&_nfls=false&removeNavigator=true&action=FAQ&userType=agency">Agency FAQs</a></li>
					<li <%=lsClassNameForTab5%>><a class="<%=lsClassNameForTab3%>"  title="Agency Help Documents" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_faq&_nfls=false&removeNavigator=true&action=helpdocumentsagency">Agency Help Documents</a></li>
				<%} %>
			<%}else{ %>
				<li><a class="selected" title="Frequently Asked Questions">Frequently Asked Questions</a></li>
			<%} %>
		</ul>
	</div>
	<div id="tabs-container">
	<!-- Form Data Starts -->
		<% if(null != lsFilePath && !("".equalsIgnoreCase(lsFilePath))){%>
			<jsp:include page="<%=lsFilePath%>"></jsp:include>
		<%} %>
	</div>
	
	<!-- Overlay Popup Starts -->
	<div class="overlay"></div>
	<div class="alert-box-contact">
		<div class="content">
				<div id="contactDiv"></div>
		</div>
	</div>
	<!-- Overlay Popup Ends -->