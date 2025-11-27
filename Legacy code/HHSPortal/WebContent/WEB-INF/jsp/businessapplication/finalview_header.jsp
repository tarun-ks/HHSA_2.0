<!--This will display the main header for application containing basic, board, filings and policies tabs-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<div>
	<c:choose>
		<c:when test="${param.bussAppStatus ne null}">
			<c:set var="bussAppStatus" value="${param.bussAppStatus}"></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="bussAppStatus" value="${bussAppStatus}"></c:set>
		</c:otherwise>
	</c:choose> 
	<c:choose>
		<c:when test="${param.business_app_id ne null}">
			<c:set var="business_app_id" value="${param.business_app_id}"></c:set>
		</c:when>
		<c:when test="${sessionScope.business_app_id ne null}">
			<c:set var="business_app_id" value="${sessionScope.business_app_id}"></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="business_app_id" value="${business_app_id}"></c:set>
		</c:otherwise>
	</c:choose> 
	<c:set var="providerId" value="${cityUserSearchProviderId}"/>
	<c:if test="${cityUserSearchProviderId eq null }">
		<c:set var="providerId" value="${user_organization}"/>
	</c:if>
	<jsp:scriptlet>
		if(null == request.getParameter("removeNavigator")){
	</jsp:scriptlet> 
	<c:choose>
		<c:when
			test="${applicationType ne null && applicationType eq 'service' }">
			<h2>Service Application</h2>
			<c:set var="headerName" value="Service"/>
			<c:set var="urlHistory"
				value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_service_post&_nfls=false&section=servicessummary&subsection=servicehistory&next_action=displayServiceApplicationHistory&business_app_id=${business_app_id}&bussAppStatus=approved&loReadOnly=false&applicationId=${applicationId }&elementId=${elementId}&service_app_id=${service_app_id }&cityUserSearchProviderId=${providerId}&app_menu_name=share"></c:set>
			<c:set var="urlApplication"
				value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&finalView=service&applicationId=${applicationId }&business_app_id=${business_app_id}&service_app_id=${service_app_id }&elementId=${elementId}&cityUserSearchProviderId=${providerId}&app_menu_name=share"></c:set>
		</c:when>
		<c:otherwise>
			<h2>Business Application</h2>
			<c:set var="headerName" value="Business"/>
			<c:set var="urlHistory"
				value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_section&_nfls=false&section=businessapplicationsummary&subsection=applicationhistory&next_action=open&business_app_id=${business_app_id}&bussAppStatus=approved&loReadOnly=false&applicationId=${applicationId }&cityUserSearchProviderId=${providerId}&app_menu_name=share"></c:set>
			<c:set var="urlApplication"
				value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&finalView=business&applicationId=${applicationId }&business_app_id=${business_app_id}&cityUserSearchProviderId=${providerId}&app_menu_name=share"></c:set>
		</c:otherwise>
	</c:choose>
		<div class='linkReturnVault' style="margin-top: -30px;">
			<c:choose>
				<c:when test="${org_type eq 'city_org'}">
					<a id="header_application123"
				href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_city_home&_st=&_windowLabel=portletInstance_12&_urlType=action#wlp_portletInstance_12"
				title="Return to Summary">Return to Summary</a>	
				</c:when>
				<c:otherwise>
					<a id="header_application123"
				href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=${userType}"
				title="Return to Summary">Return to Summary</a>
				</c:otherwise>
			</c:choose>
		
		</div>
		<div class="appnavbar" id="nyc_app_sections">
			<ul class="roundcorners">
				<li><a class="current" id="section_businessapplicationsummary"
					href="${urlApplication}"
					title="Basics">${headerName } Application Details</a></li>
				<li class="nobdr"><a class=""
					id="section_businessapplicationhistory"
					title="Business Application Summary"
					href="${urlHistory }">Application History & Comments</a></li>
			</ul>
			
		<jsp:scriptlet>
			}
		</jsp:scriptlet> 
	</div>
	
</div>
<div class="overlay"></div>
<div class="alert-box-help">
		<div id="newTabs">
                <div class="tabularCustomHead">Applications - Help Documents</div>
          <div id="helpPageDiv"></div>
          </div>  
    <a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box-contact">
	<div id="newTabs">
		<div id="contactDiv"></div>
	</div>
</div>

<div class="clear"></div>
<br class="clear"/>
<script>
//This method for adding oaverlay for asking user to save any unsaved data.
	var lastDataArray = new Array();
	$(function(){
		$("a[id!='smallA'][id!='mediumA'][id!='largeA']").click(function(e) {
			if($("#tabs-container").size() > 0
					&& !$(this).hasClass("byPassLink")
					&& ($(this).parents("#tabs-container").length == 0 || $(this).attr("id") == "returnSummaryPage")){
				var $self=$(this);
				var isSame = true;
				if(lastDataArray != null && lastDataArray.length > 0){
					$.each(lastDataArray, function(i) {
						if(!$(lastDataArray[i][1]).compare($("form[name='"+lastDataArray[i][0]+"']").serializeArray())){
							isSame = false;
						}
					});
				}
				if(!isSame && lastDataArray != null & lastDataArray.length > 0){
					e.preventDefault();
					$('<div id="dialogBox"></div>').appendTo('body')
					.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
					.dialog({
						modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
						width: 'auto', modal: true, resizable: false, draggable:false,
						dialogClass: 'dialogButtons',
						buttons: {
							OK: function () {
								document.location = $self.attr('href');
								$(this).dialog("close");
							},
							Cancel: function () {
								$(this).dialog("close");
							}
						},
						close: function (event, ui) {
							$(this).remove();
						}
					});
					$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
				}
			}
		});
	});
	//Onload Event.
	$(window).load(function(){
		var ignoreForms = ["myinboxform", "myTaskMform"];
		$("form").each(function(){
			if(typeof($(this).attr("name")) != "undefined" && $.inArray($(this).attr("name"), ignoreForms) < 0){
				lastDataArray[lastDataArray.length] = new Array($(this).attr("name"), $(this).serializeArray());
			}
		});
	});
</script>
<%
	String lsFilePath = "";
	if(renderRequest.getAttribute("fileToInclude") != null){
		lsFilePath = (String) renderRequest.getAttribute("fileToInclude");
	}
%>
<script type="text/javascript">
//show selected method.
		showSelected('${section }','${subsection }');
</script>
<div id="tabs-container">
	<!-- Form Data Starts -->
	<div id="mymain">
		<c:if test="${subsection eq 'applicationhistory' or subsection eq 'servicehistory'}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Applications');"></a></div>
	     </c:if>
	     <jsp:include page="<%=lsFilePath%>"></jsp:include>
	</div>
</div>
	