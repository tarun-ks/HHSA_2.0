<!--This will display the header for application services when application is submitted.
This jsp will include all other screen of services document, specialization and service setting etc.-->
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.ArrayList"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<%@page import="com.nyc.hhs.constants.ComponentMappingConstant"%>
<portlet:defineObjects />
<script type="text/javascript">
	var actionUrl  = '<portlet:renderURL/>'+"&section="+'${section }'+"&subsection="+'${subsection }'+"&forUpdate="+'${forUpdate}' ;
	//submits the form
	function submitForm(anchor){
		$('#'+anchor).attr("href", actionUrl) ;
	}
	var lastDataArray = new Array();
	$(function(){
		//Provides popup for Unsaved data
		$('.roundcorners a').parent().removeClass('current');
		$('#subsection_${subsection}').addClass('current');
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
			$(".current").attr("href", "#");
			$(".active").attr("href", "#");
			$(".selected").attr("href", "#");
		});
	});

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
	String lsMenu="";
	
	if(renderRequest.getAttribute(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU) != null){
		lsMenu = (String)renderRequest.getAttribute(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU);
	}
	String lsFilePath = "";
	if(renderRequest.getAttribute("fileToInclude") != null){
		lsFilePath = (String) renderRequest.getAttribute("fileToInclude");
	}
 	%>

	<script>
		<c:forEach var="map1" items="${loBusinessStatusBeanMap}">
			setStatusSection("${map1.key}", "${map1.value.msSectionStatus}");
		</c:forEach>
	</script>
		<h2>Service Application</h2>
		
		<c:if test="${deactivatedService ne null and  deactivatedService}">
		<div class="failed" style="display:block">You cannot resubmit your application as it is no longer required to apply for ${serviceName}. Please withdraw your application.</div>
		<br></br>
	</c:if>
		<div class='linkReturnVault' style="margin-top: -30px;">
			<c:choose>
				<c:when test="${org_type eq 'city_org'}">
					<a id="header_application123" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_city_home&_st=&_windowLabel=portletInstance_12&_urlType=action#wlp_portletInstance_12" title="Return to Summary">Return to Summary</a>	
				</c:when>
				<%-- Start : R5 Added --%>
				<c:when test="${org_type eq 'agency_org'}">
					<a id="header_application123"
				href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agency_home&_st=&_windowLabel=portletInstance_16&_urlType=action#wlp_portletInstance_16"
				title="Return to Summary">Return to Summary</a>	
				</c:when>
				<%-- End : R5 Added --%>
				<c:otherwise>
					<a id="header_application123" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=${userType}" title="Return to Summary">Return to Summary</a>
				</c:otherwise>
			</c:choose>
				
			</div>
		<div class="appnavbar" id="nyc_app_sections">
 			<ul class="roundcorners" >
 					<c:if test="${addNewService eq null}">
						<li><a id="subsection_questions" title="Answer questions about your organization's ability to provide this Service" href="<portlet:renderURL><portlet:param name="service_app_id" value="${service_app_id }" /><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="elementId" value="${elementId}" /><portlet:param name="section" value="servicessummary" /><portlet:param name="subsection" value="questions" /><portlet:param name="next_action" value="showServiceQuestion" /></portlet:renderURL>">Questions</a></li>
						<li><a id="subsection_documentlist" title="Upload any required documents for this Service Application" href="<portlet:renderURL><portlet:param name="elementId" value="${elementId}" /><portlet:param name="service_app_id" value="${service_app_id }" /><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="servicessummary" /><portlet:param name="subsection" value="documentlist"/><portlet:param name="next_action" value="open" /></portlet:renderURL>">Documents</a></li>
						<li><a id="subsection_specialization" title="Indicate your organization's specialization for this Service if applicable" href="<portlet:renderURL><portlet:param name="elementId" value="${elementId}" /><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="servicessummary" /><portlet:param name="subsection" value="specialization"/><portlet:param name="next_action" value="open" /></portlet:renderURL>">Specialization</a></li>
						<li><a class="" id="subsection_servicesetting" title="Indicate the setting(s) in which your organization has the ability to provide this Service" href="<portlet:renderURL><portlet:param name="elementId" value="${elementId}" /><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="servicessummary" /><portlet:param name="subsection" value="servicesetting"/><portlet:param name="next_action" value="open" /></portlet:renderURL>">Service Setting</a></li>
					</c:if>
					<li><a class="" id="section_servicessummary" title="Service Capacity" title="Service Summary" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="elementId" value="${elementId}" /><portlet:param name="section" value="servicessummary" /><portlet:param name="service_app_id" value="${service_app_id }" /><portlet:param name="subsection" value="summary"/><portlet:param name="next_action" value="checkForService" /><portlet:param name="displayHistory" value="displayHistory" /></portlet:renderURL>">Services Summary</a></li>
					
			</ul>
			<jsp:scriptlet> 
			 	if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S076_SECTION, request.getSession())){
			 </jsp:scriptlet>
			 
			<c:if test="${addNewService eq null}">
 <%-- 			
				<ul class="roundcorners" >
					<c:choose>
						<c:when test="${param.loReadOnly || loReadOnly || !applicationStatus}">
<!--   							<li><a  id="subsection_servicesubmit" class="submitButtonDisable" href="#" title="Submit the Service Application for review, once the application is complete.">Submit</a></li> --> 
						</c:when>
						<c:otherwise>
 							<li><a id="subsection_servicesubmit" title="Submit the Service Application for review, once the application is complete." href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" />
							<portlet:param name="service_app_id" value="${service_app_id }" />
							<portlet:param name='section' value='serviceapplicationsummary'/>
							<portlet:param name='subsection' value='applicationSubmit'/><portlet:param name="next_action" value="open" /><portlet:param name="bussAppStatus" value="${portletSessionScope.bussAppStatus }" />
							</portlet:renderURL>">Submit</a></li>
						</c:otherwise>
					</c:choose>
				</ul> --%>
				
			</c:if>
			
			<jsp:scriptlet>
			}
		</jsp:scriptlet> 
	   </div>
	   <div class='clear'>&nbsp;</div>
	   <c:if test="${param.displayHistory eq 'displayHistory' or displayHistory eq 'displayHistory'}">
 		<div class="customtabs">
 			<div class="floatRht capitalize">
				<b>Status: </b>${portletSessionScope.bussAppStatus}
			</div>
 			<ul>																																					
				<li><a id="subsection_summary" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_service_post&_nfls=false&section=servicessummary&subsection=summary&next_action=checkForService&service_app_id=${service_app_id}&bussAppStatus=${portletSessionScope.bussAppStatus}&business_app_id=${business_app_id }&loIsDisplay=${loIsDisplay}&displayHistory=displayHistory&elementId=${elementId}" title="Service Summary">Service Summary</a></li>
				<jsp:scriptlet> 
				// Start : R5 Added
				if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S062_PAGE, request.getSession())){
				//End : R5 Added
				</jsp:scriptlet>
				<li><a id="subsection_servicehistory" title="Service History & Comments" href="<portlet:renderURL>
				<portlet:param name="business_app_id" value="${business_app_id }" />
				<portlet:param name="service_app_id" value="${service_app_id }" />
				<portlet:param name='section' value='${section}'/>
				<portlet:param name='subsection' value='servicehistory'/>
				<portlet:param name="next_action" value="displayServiceApplicationHistory" />
				<portlet:param name="displayHistory" value="displayHistory" />
				<portlet:param name="bussAppStatus" value="${portletSessionScope.bussAppStatus}" />
				<portlet:param name="elementId" value="${elementId}" />
				</portlet:renderURL>">Service History & Comments</a></li>
				<jsp:scriptlet> 
				// Start : R5 Added
					}
				//End : R5 Added
				</jsp:scriptlet>
			</ul>
	   </div>
	   <script type="text/javascript">
			showSelected('${section }','${subsection }');
		</script>
	</c:if>
   <div id="tabs-container">
<!-- Form Data Starts -->
<div id="mymain">
	<!-- Updated for release 3.3.0, Defect 6449: Updated to display Help Icon for View Information Screen - Removed Check to hide Help Icon foe 'isViewDoc' flag value ne 'Yes' -->
	<div class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Applications');"></a></div>
	<jsp:include page="<%=lsFilePath%>"></jsp:include>
</div>
</div>
<div class="overlay"></div>
<div class="alert-box-help">
    <div class="content">
          <div id="newTabs" class='wizardTabs'>
                <div class="tabularCustomHead">Applications - Help Documents</div>
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
