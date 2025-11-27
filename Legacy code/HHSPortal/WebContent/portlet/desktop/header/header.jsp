<%@page import="java.util.Hashtable"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.List" %>
<%@page import="com.nyc.hhs.constants.ApplicationConstants" %>
<%@page import="com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb,com.nyc.hhs.model.ProviderBean,com.nyc.hhs.util.ActionStatusUtil,org.apache.commons.lang.StringEscapeUtils,com.nyc.hhs.util.FileNetOperationsUtils" %>
<%@page errorPage="/error/errorpage.jsp" %>
<%@page import=",com.nyc.hhs.constants.*,com.nyc.hhs.util.CommonUtil,com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.TaskQueue"%>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.util.HHSUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nyc.hhs.util.PropertyLoader"%>
<%@page import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
<%@page import="com.nyc.hhs.frameworks.sessiongrid.*"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/sessiongrid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
#greyedBackground 
{
    position: fixed;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    margin: auto;
    margin-top: 0px;
    width: 100%;
    height: 100%;
    background : none repeat scroll 0 0 #fff;
    z-index: 9999;
	opacity: 0.8;
	filter: alpha(opacity = 80);
	text-align:center;
	vertical-align:middle;
     } 
 #greyedBackground img
 {
	background-color:#fff;
	top:48%;
	position:relative;
	opacity: 0.8;
 }
 /* Added for Release 3.4.0, #5681 - For Print Budget Screen - Starts*/
 #greyedBackgroundPrintBudget 
{
    position: fixed;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    margin: auto;
    margin-top: 0px;
    width: 100%;
    height: 100%;
    background : none repeat scroll 0 0 #fff;
    z-index: 9999;
	opacity: 0.8;
	filter: alpha(opacity = 80);
	text-align:center;
	vertical-align:middle;
     } 
 #greyedBackgroundPrintBudget img
 {
	background-color:#fff;
	top:48%;
	position:relative;
	opacity: 0.8;
 }
 /* Added for Release 3.4.0, #5681 - For Print Budget Screen - Ends*/
 
 /* Added for SAML NYCID header - Starts R8.0.0 */
 		body {
            margin: 0;
        }
        
        a img {
	        border: 0;
        }

        .container-nycidm {
            *zoom: 1;
            max-width: 940px;
            padding-left: 20px;
            padding-right: 20px;
            margin-left: auto;
            margin-right: auto;
            position: relative;
        }
        
        .container-nycidm: after {
            content: "";
            display: table;
            clear: both
        }

        .container-nycidm: before, .container: after {
            display: table;
            line-height: 0;
            content: ""
        }

        .container-nycidm: after {
            clear: both;
        }

        .nycidm-header .upper-header-black {
            background: #000;
            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
            height: 25px;
            color: #fff;
            font-size: 12px;
            font-weight: bold;
            padding: 0;
        }

        .nycidm-header .upper-header-black a {
            color: #fff;
            text-decoration: none;
        }

        .nycidm-header .small-nyc-logo {
            height: 15px;
            width: 40px;
            position: relative;
            top: -4px;
        }

        .nycidm-header .upper-header-black-title {
            position: relative;
            top: -7px;
            color: #fff;
			font-size: 12px;
        }

        .nycidm-header .upper-header-b, .nycidm-header .upper-header-a {
            float: right;
            padding-top: 5px;
        }

        .nycidm-header .vert-divide {
            margin: 0 10px;
        }

        .vert-divide-right {
            float: right;
            margin: 0 10px;
        }

        @media only screen and (max-width:767px) {
            .nycidm-header .upper-header-black-title {
                display: none;
            }
        }
  /* Added for SAML NYCID header - Ends R 8.0.0  */
 
</style>
<!-- [Start] 9.6.0 QC 9619 Update text to include Edge in Enable Javascript screen and fix misleading Enable Javascript screen for Microsoft Edge (even the javascript is enabled) -->
<noscript>
	<jsp:include page="/portlet/accountrequestlogin/missingprofileinfo/javascriptrequired.jsp" />
	<style>
	#mymain {
	display:none;
	}
	#nyc_header_div{
	display:none;
	}
	.breadcrumb {
	display:none;
	}

	</style>
</noscript>
<script type="text/javascript">	
    history.forward(1);
	$(document).ready(function(){
		$(".current").attr("href", "#");
		$(".active").attr("href", "#");
		$(".selected").attr("href", "#");
		 if(document.getElementById("mymain")!=null && ($("#mymain").html().indexOf("TransactionManager")!=-1 || $("#mymain").html().indexOf("org.springframework.web.portlet.FrameworkPortlet")!=-1)){
				var url = "${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&menuVar=1&_nfls=false&_pageLabel=portlet_hhsweb_portal_page_errorpage";
			         location.href=url;
		 }
	});
	var appStatusArray = new Array();
	function AppStatusObj(elementId, currentStatus){
		this.elementId = elementId ; 
		this.currentStatus = currentStatus ;
	}
	
	function changeStatus(elementId, newStatus){
		var element = appStatusArray[elementId];
		element.currentStatus = newStatus;
		$('#'+elementId).addClass(newStatus);
	}
	function showHeaderSelected(id){
		$('#nyc_header_div a, #nyc_header_div li').removeClass('active');
		$('#'+id).addClass('active');
	}
	function setStatusSection(section, status){
		$('#section_'+ section).addClass(status);
	}
	function showSelected(section, subsection){
		showHeaderSelected('header_application');
		$('#nyc_app_sections a').removeClass('current');
		$('#section_'+section).addClass('current');
		$('#customtabs a').parent().removeClass('selected');
		$('#subsection_'+subsection).parent().addClass('selected');
	}
	function showSelectedOrg(section, subsection,headerName){
		showHeaderSelected(headerName);//'header_application'
		$('#nyc_app_sections a').removeClass('current');
		$('#customtabs a').parent().removeClass('current');
		$('#section_'+section).addClass('current');
		$('#subsection_'+subsection).parent().addClass('selected');
	}
	function showSelectedForProvider(section, subsection){
		showHeaderSelected('header_organization_information');
		$('#customtabs a').parent().removeClass('selected');
		$('#subsection_'+subsection).parent().addClass('selected');
	}
	function getmycookie(myname)
	//this function is called by the function mydefaultsize()
	//this function merely looks for any previously set cookie and then returns its value
	{
	//if any cookies have been stored then
	if (document.cookie.length>0)
	{
		//where does our cookie begin its existence within the array of cookies  
		mystart=document.cookie.indexOf(myname + "=");
		//if we found our cookie name within the array then
		if (mystart!=-1)
		 {
			 //lets move to the end of the name thus the beginning of the value
			 //the '+1' grabs the '=' symbol also
			 mystart=mystart + myname.length+1;
			 //because our document is only storing a single cookie, the end of the cookie is found easily
			 myend=document.cookie.length;
			 //return the value of the cookie which exists after the cookie name and before the end of the cookie
			 return document.cookie.substring(mystart,myend);
		 }
	}
	//if we didn't find a cookie then return nothing  
	return "";
	}
	
	function mydefaultsize(){
	//this function is called by the body onload event
	//this function is used by all sub pages visited by the user after the main page
	var div = document.getElementById("mymain");
	//call the function getmycookie() and pass it the name of the cookie we are searching for
	//if we found the cookie then
		if (getmycookie("mysize")>0)
		{
		//apply the text size change	
		div.style.fontSize = getmycookie("mysize") + "px";
		}
	}
	
	// This method opens the overlay to Switch Organization.
	function switchaccount(){
		var url = "${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_42&_urlType=resource&_portlet.renderResource=true&_resId=getSelectOrganization&launchOverlay=true";
		pageGreyOut();
		var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
				if (response != null || response != '') {
					$("#switchAccountDiv").html(response);
					removePageGreyOut();
				}
				$(".overlay").launchOverlay($(".alert-box-switchAccountDiv"), $(".exit-panel.exit-switchAccount"), "650px", null, "onReadyMethod");
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
	
	
	/*[Start] QC 8914 R7.2.0 invoke pop-up through URL for Switching Role  */ 
	// This method opens the overlay to Switch role.
 	function switchrole(){
		var url = "${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_windowLabel=portletInstance_52&_urlType=resource&_portlet.renderResource=true&_resId=getSelectRole&launchOverlay=true";
		pageGreyOut();
		var jqxhr = $.ajax({
			url : url,
			type : 'POST',
			cache : false,
			success : function(response) {
				if (response != null || response != '') {
					$("#switchRoleDiv").html(response);
					removePageGreyOut();
				}
				$(".overlay").launchOverlay($(".alert-box-switchRoleDiv"), $(".exit-panel.exit-switchRole"), "650px", null, "onReadyMethod");
			},
			error : function(data, textStatus, errorThrown) {
				showErrorMessagePopup();
				removePageGreyOut();
			}
		});
	}
	/*[End] QC 8914 R7.2.0 invoke pop-up through URL for Switching Role  */

	
</script>

<div style="display:none;" id="greyedBackground"><img src='../framework/skins/hhsa/images/loadingBlue.gif' /></div>
<!-- Added for Release 3.4.0, #5681 - For Print Budget Screen-->
<div style="display:none;" id="greyedBackgroundPrintBudget"><img src='../framework/skins/hhsa/images/loadingBlue.gif' /></div>

<%
	List<ProviderBean> loProviderBeanList = (List<ProviderBean>) BaseCacheManagerWeb.getInstance().getCacheObject(ApplicationConstants.PROV_LIST);
String lsProviderNameFromCache = null;
if(null != loProviderBeanList && !loProviderBeanList.isEmpty()){
	lsProviderNameFromCache = StringEscapeUtils.unescapeJava(FileNetOperationsUtils.getProviderName(loProviderBeanList,(String)session.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG)));
}
if (lsProviderNameFromCache == null || lsProviderNameFromCache.trim().length()==0){
	lsProviderNameFromCache = StringEscapeUtils.unescapeJava((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME));
}
 String userTypeInSession = ApplicationConstants.PROVIDER_ORG;
   if(request.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE)!=null){
       userTypeInSession = (String)request.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
    }
%>
<input type="hidden" value="${sessionTimeOutValue}" id="sessionTimeOut"/>
<input type="hidden" value="${sessionTimeOutValueLogin}" id="sessionTimeOutLogin"/>
<input type="hidden" name="contextPathSession" id="contextPathSession" value="${pageContext.servletContext.contextPath}" />
<input type="hidden" value="<%= userTypeInSession%>" id="typeOfUser"/>



<%
	String lsUserValidated = (String)session.getAttribute("userValidated");
	String lsOrgId = (String) session.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
	String lsRemoveMenu = request.getParameter("removeMenu");
	
	/*R9.4.0 QC9720*/
	String lsAgencyNotice = " ";
    if( ActionStatusUtil.pullNotificationByAgency( lsOrgId ) != null  ){
    	lsAgencyNotice = ActionStatusUtil.pullNotificationByAgency(lsOrgId) ;
    }
    /*R9.4.0 QC9720*/


	// R 8.0.0 get KEY_SESSION_ORG_TYPE
	String lsOrgType = (String) session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
	if(lsRemoveMenu == null){
 	%>
 
 	<!-- R 8.0.0   QC-9317 new NCID header *********/-->
	<div class="nycidm-header">
		<div class="upper-header-black">
			<div class="container-nycidm">
				<span class="upper-header-left"> <img class="small-nyc-logo"
					alt=""
					src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/nyc_white.png?v=1" />
					<img class="vert-divide" alt=""
					src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/upper-header-divider.gif?v=1" />	
					<span class="upper-header-black-title">
                    	HHS Accelerator
                	</span>
				</span> 
				<c:choose>
					<c:when test="${userValidated == true}">
						<img class="vert-divide-right" alt=""
							src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/upper-header-divider.gif?v=1" />
						<!-- QC 9205 R 8.0.0 Display Profile link only for Providers -->	
						<% if(ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsOrgType)) { %>	
							<span class="upper-header-b"> 
								<a href="<%=HHSUtil.obtainNYCIDurl()%>" target="_blank">NYC.ID Profile</a>
							</span>
						<%} %>	
					</c:when>
				</c:choose>
			</div>
		</div>
	</div>
	<!-- NYC Gov topbar End -->
	
	<!-- HHS Header Start -->
	<%
	if(lsUserValidated != null && lsUserValidated.equalsIgnoreCase("true") && session.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG) != null
			&& (request.getAttribute("userExitInSession")==null &&  request.getAttribute("userDoesNotBelong")==null &&  request.getAttribute("userDoesNotBelongToOrg")==null)){
	%>
		<c:choose>
			<c:when test="${org_type eq 'city_org'}">
				<c:set var="userType" value="accelerator" scope="application"></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="userType" value="provider" scope="application"></c:set>
			</c:otherwise>
		</c:choose>
		<%-- Start changes for R5 --%>
		<div id="nyc_header_div" class="hhs_header reportHeader">
			<!-- Start  QC 9341 R 8.0.0 Make HSA Heading clickable -->
			<!-- <h1 title="HHS Accelerator">Human Health Services Accelerator</h1> -->
			 <a  
					<%if(session.getAttribute("MissinProfileHeader")!=null){%> href="javascript:void(0)"<%} 
						if(session.getAttribute("MissinProfileHeader")==null && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(userTypeInSession) ){%>href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_provider_home&_nfls=false&app_menu_name=home_icon" <%} %>
				        <% if(session.getAttribute("MissinProfileHeader")==null && ApplicationConstants.CITY_ORG.equalsIgnoreCase(userTypeInSession) ){%>href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_city_home&_nfls=false&app_menu_name=home_icon" <%} %>
				        <% if(session.getAttribute("MissinProfileHeader")==null && ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(userTypeInSession) ){%>
				       	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HEADER_AGENCY, request.getSession())){%>
				        		href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_agency_r1&_nfls=false&app_menu_name=home_icon" 
				       	<%}else{ %>
				        		href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agency_home&_nfls=false&app_menu_name=home_icon" 
				       	<%} %>
				        <%} %>
			 ><h1 title="HHS Accelerator">Human Health Services Accelerator</h1></a>
			 
			<!-- End QC 9341 R 8.0.0 Make HSA Heading clickable -->
			
			<script>
                function clickedMe() {
                document.getElementById('h2tag').innerHTML = 'Alas ! I am clicked!'
            }
            </script>
			<%if(session.getAttribute("MissinProfileHeader")==null){%>
			    <ul id="nyc_header_ul" class="emptyClass reportHeaderUl">
				    <%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HE_N01_SECTION_1, request.getSession())){%>
				    	<!-- Updated in 3.1.0. Added check for Defect 6346, adding a identifier for Tab Access from main header.-->
				    	<li><a id="header_organization_information" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_organizationPprofile&_nfls=false&app_menu_name=header_organization_information&section=basics&subsection=questions&next_action=showquestion&action=orgBasicInformation&fb_formName=OrgProfile&isHeaderTab=isHeaderTab" title="Access and edit basic information about your organization">Organization Information</a>
				    	</li>
					<%} %>
					<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HE_N01_SECTION_2, request.getSession())){%>
				    	<li><a id="header_document_vault" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=enhanced_document_vault_page&_nfls=false&app_menu_name=header_document_vault&removeNavigator=true&headerClick=true" title="Upload, download, and share your organization's documents" >Document Vault</a>
				        </li>
					<%} %>
				    <%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HE_N01_SECTION_3, request.getSession())){%>
				    	<li><a id="header_application" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=${userType}" title="Apply for approval to receive solicitations in HHS Accelerator by answering a series of questions and uploading documentation" >Applications</a></li>
				    <%} %>
				    <!-- QC 8914 R 7.2.0 read only role - do not display Maintenance tab for user with read only flag -->
				    <%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HE_N01_SECTION_4, request.getSession()) && !CommonUtil.hideForOversightRole(request.getSession()) ){%>
				    	<li><a id="header_maintenance" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenancelanding&_nfls=false&app_menu_name=header_maintenance" title="Access maintenance screens for Taxonomy and FAQ's" >Maintenance</a></li>
				    <%} %>
				    
				   <%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HEADER_AGENCYP, request.getSession())){%>
					     <li><a id="header_procurement" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&resetSessionProcurement=true" title="Access procurement details, RFPs and proposals" >Procurements</a></li>
					<%}if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HEADER_AGENCYF, request.getSession())){%>     
					     <!--Made changes for Emergency Build 4.0.1 defect 8360 -->
					     <li><a id="header_financials" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_financials&_nfls=false&app_menu_name=header_financials&fromFinancialTab=true" title="Manage budgets, invoices and payments">Financials</a></li>
				    <%} %>
				   <c:set var="AGENCY_S405_PAGE"><%=HHSComponentMappingConstant.AGENCY_S405_PAGE%></c:set>
  				   <d:content section="${AGENCY_S405_PAGE}">
  				     <li><a id="header_agencySettings" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceagencysettings&agencysettinglogin=agencylogin" title="">Agency Settings</a></li>
				   </d:content> 
			    </ul>
			    <%}%>
			<!-- toolbar start -->
			<div class="toolbar reportToolBar">
				<div class="textresize">
					<ul>
				        <li><a onclick="changemysize(this, 10);" href="javascript:void(0);"  title="Small Text Size" id="smallA">A</a></li>
				        <li><a onclick="changemysize(this, 12);" href="javascript:void(0);"  title="Medium Text Size" id="mediumA" class="textmedium">A</a></li>
				        <li><a onclick="changemysize(this, 14);" href="javascript:void(0);"  title="Large Text Size" id="largeA" class="textbig">A</a></li>
				    </ul>
				    <input type='hidden' name='aaaValueToSet' id='aaaValueToSet' value='${aaaValueToSet}' />
				    <input type='hidden' name='urlForAAA' id='urlForAAA' value='${pageContext.servletContext.contextPath}/saveAAASize?next_action=aaaValueToSet' />
				    <span>Text Size:</span>
				</div>
				
				<ul id="toolsIconUlID" class='toolbarIcons emptyClass'>
					<li id="home_icon" class="homeicon"><a  
						<%if(session.getAttribute("MissinProfileHeader")!=null){%>
                           href="javascript:void(0)"
				        <%} 
						if(session.getAttribute("MissinProfileHeader")==null && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(userTypeInSession) ){%>href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_provider_home&_nfls=false&app_menu_name=home_icon" <%} %>
				        <% if(session.getAttribute("MissinProfileHeader")==null && ApplicationConstants.CITY_ORG.equalsIgnoreCase(userTypeInSession) ){%>href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_city_home&_nfls=false&app_menu_name=home_icon" <%} %>
				        <% if(session.getAttribute("MissinProfileHeader")==null && ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(userTypeInSession) ){%>
				       	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HEADER_AGENCY, request.getSession())){%>
				        		href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_agency_r1&_nfls=false&app_menu_name=home_icon" 
				       	<%}else{ %>
				        		href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agency_home&_nfls=false&app_menu_name=home_icon" 
				       	<%} %>
				        <%} %>
				        title="Home" >Home</a>
					</li>
					
				    <%if(session.getAttribute("MissinProfileHeader")==null){%>
				    <li id="report_icon" class="inboxicon reporticon"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_report&_nfls=false&app_menu_name=BIRT&removeNavigator=true&reportType=financials" title="Reports">Reports</a></li>
				    
				<!-- Begin QC8914 R7.2.0 Oversight Role Hide Task Button -->
				
				<% 
				 if(! CommonUtil.hideForOversightRole(request.getSession()))
				{%>  
				 
				        <% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HE_N01_SECTION_6, request.getSession())){%>
				        	<li id="inbox_icon" class="inboxicon"><a id="inbox_icon" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_task&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox" title="Inbox">Inbox</a></li>
				        <%} %>
				       <%if(!CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HEADER_AGENCY, request.getSession()) && ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(userTypeInSession)){%>
				        	<li id="inbox_icon" class="inboxicon"><a id="inbox_icon" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agencyWorkflow&_nfls=false&app_menu_name=inbox_icon&usewindow=inbox" title="Inbox">Inbox</a></li>
				        <%} 
				        if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HE_N01_SECTION_5, request.getSession())){
				        	//Start : R5 Added
				        	Date loLastUpdateTime = (Date) session.getAttribute(HHSR5Constants.ALERT_UPDATE_TIME);
				        	if(null != loLastUpdateTime){
				        		Date loCurrenTime = new Timestamp(new Date().getTime());
								long llTimeDiff = loCurrenTime.getTime() - loLastUpdateTime.getTime();
								long llDiffMinutes = llTimeDiff
										/ (HHSR5Constants.TEMPORARY_FOLDER_CLEAN_TIME_MIN * HHSR5Constants.INITIAL_TASK_ID)
										% HHSR5Constants.TEMPORARY_FOLDER_CLEAN_TIME_MIN;
								if (llDiffMinutes > HHSR5Constants.INT_ONE){
									%><script type='text/javascript'>$(document).ready(function(){UpdateAlertInbox();});</script>
				        	<%}else{
							%>
							<script type='text/javascript'>
							$(document).ready(function(){
								var _alertCount = '<%=(String)session.getAttribute(HHSR5Constants.ALERT_BOX_UN_READ_DATA)%>';
								if(_alertCount != 0)
								{
									$('.alert_msg').text(_alertCount);
								}
							});</script>
							<%
				        	}
				        	}
				        	else{
				        		%><script type='text/javascript'>$(document).ready(function(){UpdateAlertInbox();});</script> <%
				        	}
				        	//End : R5 Added
				        	%>
				        	<li id="alert_icon" class="alerticon"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_alert&_nfls=false&app_menu_name=alert_icon&next_action=showpage" title="Alerts">Alerts</a>
				        	<span class="alert_msg"></span>
				        	</li>
				        <%}%>
			      <%} else {%>
					  <li id="empty_disabled_icon" class="empty_disabled_icon" ><a id="empty_disable_icon"  title=" "  disabled"="disabled">empty</a></li>				        
			     <%}%> 
				      <!-- End QC8914 Oversight --> 
				       
				        <li id="help_icon" class="helpicon"><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_faq&_nfls=false&removeNavigator=true&action=FAQ&app_menu_name=help_icon" title="Help">Help</a></li>
						
						<%if((session.getAttribute("MissinProfileHeader")==null && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(userTypeInSession)) && (session.getAttribute("ShowAccountSwitchIcon")!=null)){%>
						<li id="switchaccount_icon" class="switchaccounticon"><a href="#" onclick="switchaccount()" title="Switch user">Switch User</a></li>
					<%}} %>
					
				  
				    <%--
				    start QC 8914 R 7.2.0 add Oversight Switch icon for agency and city user
				     --%>
				      <%if( "1".equalsIgnoreCase( (String)session.getAttribute(ApplicationConstants.KEY_SESSION_OVERSIGHT_FLAG)) 
				    		 && ApplicationConstants.AGENCY_ORG.equalsIgnoreCase( (String)session.getAttribute(HHSR5Constants.ORG_TYPE_ORIGINAL)) 
				    		 ){%>
														  						
						<li id="switchrole_icon" class="switchaccounticon">
					    	<a href="#" onclick="switchrole()" title="Switch role">Switch Oversight</a>  
					    </li> 
											
					<%} %>	
					
					<!-- end QC 8914 R 7.2.0 add Oversight Switch icon for agency and city user -->
								
					
			   	 <% if(ApplicationConstants.CITY_ORG.equalsIgnoreCase(userTypeInSession)||ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(userTypeInSession) )
			   	 	{%>
				    <!-- <!-- start QC 9205 R 8.0.0 Imternal SAML 
				    <li id="logout_icon" class="logouticon"><a  href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&siteminderLogout=siteminderLogout" title="Log Out">Logout</a></li>
				    -->
				    <li id="logout_icon" class="logouticon"><a  href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout" title="Log Out">Logout</a></li>
				   <!-- <!-- end QC 9205 R 8.0.0 Imternal SAML  -->
				    <%}
			   	 	else 
			   	 	{ %>
				     <li id="logout_icon" class="logouticon"><a  href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout" title="Log Out">Logout</a></li>
				    <%} 
				  %>
				    
					
				</ul>
			</div>
			<!-- toolbar ends -->
		</div>
	 	<!-- HHS Breadcrumb Starts -->
		<div class="breadcrumb">
			<!--  Breadcrumb need to be used only for related section  -->
		<!-- Begin QC8914 R7.2.0 Oversight Role display proper name -->
			
		<%
		if(CommonUtil.hideForOversightRole(request.getSession()) && ApplicationConstants.AGENCY_ORG.equalsIgnoreCase( (String)session.getAttribute(HHSR5Constants.ORG_TYPE_ORIGINAL)) )
		{%>	
			<%if( ApplicationConstants.AGENCY_ORG.equalsIgnoreCase((String)session.getAttribute(HHSR5Constants.ORG_TYPE_ORIGINAL)) ){ %>
				<%if(null != session.getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME)){ %>
				<!--/*[Start] R9.4.0 QC9720*/  -->
	    <table style="width:100%">
	            <tbody>
	            <tr> <td>
	                  <div class="loginmessageboard">
	                    <span> 
							<% if(ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(userTypeInSession) ){%>
								<%=( (ActionStatusUtil.pullNotificationByAgency(ApplicationConstants.PROVIDER) == null) ? " " : ActionStatusUtil.pullNotificationByAgency(ApplicationConstants.PROVIDER) ) %>
							<%}%>
	                    </span> </div></td>
	            <td>		
				<div class="logininfo">
				    <span class="bold">Welcome:</span>
					<span id=''><%=session.getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME_ORIGINAL)%><%if(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME_ORIGINAL)!=null && 
							!(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME_ORIGINAL).toString().equalsIgnoreCase(""))){%><label>&sbquo;</label><%}%></span>
					<span><% if(ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(userTypeInSession) ){%><%=lsProviderNameFromCache%><%}%>
						<label> READ ONLY</label>
					</span>
				</div>
		        </td>
	            </tr>
               </tbody>
	      </table>
				<!--/*[End]R9.4.0 QC9720*/  -->
				<%} %>
			<%} 
			else 
			{ %>
				<%if(null != session.getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME)){ %>
				<!--/*[Start] R9.4.0 QC9720*/  -->
	    <table style="width:100%">
	            <tbody>
	            <tr> <td>
	                  <div class="loginmessageboard">
	                    <span >    
							<% if(ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(userTypeInSession) ){%>
								<%=( (ActionStatusUtil.pullNotificationByAgency(ApplicationConstants.PROVIDER) == null) ? " " : ActionStatusUtil.pullNotificationByAgency(ApplicationConstants.PROVIDER) ) %>
							<%}%>
							<% if(ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(userTypeInSession) ){%><%=lsAgencyNotice%><%}%>
                        </span>  
                      </div></td>  
	            <td>
				<div class="logininfo"><span class="bold">Welcome:</span>
					<span id=''><%=session.getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME)%><%if(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME)!=null && 
							!(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME).toString().equalsIgnoreCase(""))){%><label>&sbquo;</label><%}%>
					</span>
					<span>
					<% if(ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(userTypeInSession) ){%><%=lsProviderNameFromCache%><%}%>
					<% if(ApplicationConstants.CITY_ORG.equalsIgnoreCase(userTypeInSession) ){%>HHS Accelerator<%}%>
					<% if(ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(userTypeInSession) ){%>
					         <%=StringEscapeUtils.unescapeJava((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME))%>
					<%}%>
					<label> READ ONLY</label>
					</span>
				</div>
		        </td>
	            </tr>
               </tbody>
	      </table>					
				<!--/*[End]R9.4.0 QC9720*/  -->
				<%} %>
			<%}
			%>	
							
		<%} 
		else 
		{ %>
			<%if(null != session.getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME)){ %>
				<!--/*[Start] R9.4.0 QC9720*/  -->
	    <table style="width:100%">
	            <tbody>
	            <tr> <td>
	                  <div class="loginmessageboard">
	                    <span >
							<% if(ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(userTypeInSession) ){%>
								<%=( (ActionStatusUtil.pullNotificationByAgency(ApplicationConstants.PROVIDER) == null) ? " " : ActionStatusUtil.pullNotificationByAgency(ApplicationConstants.PROVIDER) ) %>
							<%}%>
							<% if(ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(userTypeInSession) ){%><%=lsAgencyNotice%><%}%>                   
						</span>
					   </div>
				</td>
	            <td>		
				<div class="logininfo"><span class="bold">Welcome:</span>
					<span id=''><%=session.getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME)%><%if(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME)!=null && 
							!(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME).toString().equalsIgnoreCase(""))){%><label>&sbquo;</label><%}%></span>
					<span><% if(ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(userTypeInSession) ){%><%=lsProviderNameFromCache%><%}%>
					<% if(ApplicationConstants.CITY_ORG.equalsIgnoreCase(userTypeInSession) ){%>HHS Accelerator<%}%>
					<% if(ApplicationConstants.AGENCY_ORG.equalsIgnoreCase(userTypeInSession) ){%><%=StringEscapeUtils.unescapeJava((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME))%><%}%>
					<% if("1".equalsIgnoreCase( (String)session.getAttribute(ApplicationConstants.KEY_SESSION_OVERSIGHT_FLAG)) && ApplicationConstants.CITY_ORG.equalsIgnoreCase(userTypeInSession) ){%> READ ONLY<%}%>
					</span>
				</div>
		        </td>
	            </tr>
               </tbody>
	      </table>
				<!--/*[End]R9.4.0 QC9720*/  -->
			<%} %>
		<%}
		%>
		
		</div>
		<!-- HHS Breadcrumb Ends -->
		<!-- Code Fix for defect 1768. Added ErrorPage.jsp on page load with display:none -->
		<div style="display:none">
		<jsp:include page="/error/errorpage.jsp"></jsp:include>
		</div>

	<%
	}
	
	%>
		<!-- Switch Account - Select Organization Overlay starts --> 
		<div class="overlay"></div>
		<div class="alert-box alert-box-switchAccountDiv">
				<div id="switchAccountDiv"></div>
			    <a  href="javascript:void(0);" class="exit-panel exit-switchAccount">&nbsp;</a>
		</div>
		<!-- Switch Account - Select Organization Overlay ends -->
		
		
		<!-- QC8914 Switch Role - Select Role Overlay starts --> 
		<div class="overlay"></div>
		<div class="alert-box alert-box-switchRoleDiv">
				<div id="switchRoleDiv"></div>
			    <a  href="javascript:void(0);" class="exit-panel exit-switchRole">&nbsp;</a>
		</div>
		<!-- QC8914 Switch Role - Select Role Overlay ends -->
		
		
	<%
	}
	if(session.getAttribute("app_menu_name") != null){
		String lsValue = (String)session.getAttribute("app_menu_name");
		%>
		<script type="text/javascript">
			showHeaderSelected('<%=lsValue%>');
		</script>
		<%
	}
 %>
 
<!-- Start QC 9587 R 8.10.0 add MOCS Contact button for Provider   -->

 <% if(ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(userTypeInSession)) { %>	
  
 <script data-jsd-embedded data-key="<%=HHSUtil.obtainNYCIDurl(ApplicationConstants.JIRA_DATA_KEY) %>" 
 		 data-base-url="https://jsd-widget.atlassian.com" 
 		 src="https://jsd-widget.atlassian.com/assets/embed.js">

 </script>
<%} %>	
<!-- End QC 9587 R 8.10.0 add MOCS Help button for Provider -->

