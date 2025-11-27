<!--This will display the main header for application containing basic, board, filings and policies tabs-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.ComponentMappingConstant"%>
<div>
<input type="hidden" value="${isActiveCeo}" name="isActiveCeo">
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
	</c:choose> <c:set var="url"
		value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_section&_nfls=false&bussAppStatus=${bussAppStatus}&business_app_id=${business_app_id}"></c:set>
	<c:set var="urlApplication"
		value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_add_service&_nfls=false&bussAppStatus=${bussAppStatus}&business_app_id=${business_app_id}&applicationId=${applicationId}"></c:set>
	<jsp:scriptlet>
	if(null == request.getParameter("removeNavigator")){
		</jsp:scriptlet> 
		<c:choose>
			<c:when
				test="${headerPostService eq null and (loReadOnlyStatus eq 'draft' or loReadOnlyStatus eq 'Draft') and (addNewService eq null)}">
				<h2>Application</h2>
			</c:when>
			<c:when
				test="${addNewService ne null or headerPostService ne null}">
				<h2>Service Application</h2>
			</c:when>
			<c:otherwise>
				<h2>Business Application</h2>
			</c:otherwise>
		</c:choose>
		<div class='linkReturnVault' style="margin-top: -30px;">
			<c:choose>
				<c:when test="${org_type eq 'city_org'}">
					<a id="header_application123"
				href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_city_home&_st=&_windowLabel=portletInstance_12&_urlType=action#wlp_portletInstance_12"
				title="Return to Summary">Return to Summary</a>	
				</c:when>
				<%-- Start : R5 Added --%>
				<c:when test="${org_type eq 'agency_org'}">
					<a id="header_application123"
				href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agency_home&_st=&_windowLabel=portletInstance_16&_urlType=action#wlp_portletInstance_16"
				title="Return to Summary">Return to Summary</a>	
				</c:when>
				<%-- End : R5 Added --%>
				<c:otherwise>
					<a id="header_application123"
				href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=${userType}"
				title="Return to Summary">Return to Summary</a>
				</c:otherwise>
			</c:choose>
		
		</div>
		<div class="appnavbar appnavbarApplication" id="nyc_app_sections">
		<c:if test="${removeTabs eq null }">
			<ul class="roundcorners">
				<li><a class="current" id="section_basics"
					href="${url}&section=basics&subsection=questions&next_action=showquestion&isActiveCeo=${isActiveCeo}"
					title="Enter basic information about your organization">Basics</a></li>
				<li><a class="" id="section_filings" 
					href="${url}&section=filings&subsection=questions&next_action=showquestion&isActiveCeo=${isActiveCeo}"
					title="Enter information about your organization's tax and other regulatory filings">Filings</a></li>
				<li><a class="" id="section_board" 
					href="${url}&section=board&subsection=questions&next_action=showquestion&isActiveCeo=${isActiveCeo}"
					title="Enter information about your organization's board">Board</a></li>
				<li><a class="" id="section_policies" 
					href="${url}&section=policies&subsection=questions&next_action=showquestion&isActiveCeo=${isActiveCeo}"
					title="Enter information about your organization's policies and procedures">Policies</a></li>
				<li class=""><a class=""
					id="section_businessapplicationsummary"
					href="${url}&section=businessapplicationsummary&subsection=applicationsummary&next_action=showquestion&isActiveCeo=${isActiveCeo}"
					title="View summary information for all four sections of the Business Application: Basics, Filings, Board, and Policies">Business Application Summary</a></li>
			</ul>
		</c:if>
			<c:if
				test="${loReadOnlyStatus eq 'draft' or loReadOnlyStatus eq 'Draft'}">
				<ul class="roundcorners">
					<li class="nobdr"><a class="${serviceSummaryStatus}" id="section_servicessummary"
						href="${urlApplication}&section=servicessummary&subsection=summary&next_action=checkForService"
						title="View summary information for all Service Applications in progress">Services Summary</a></li>
				</ul>
			</c:if> 
			<jsp:scriptlet> 
			 	if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S076_SECTION, request.getSession())){
			 </jsp:scriptlet>
			 <%-- 
			<ul class="roundcorners">
				<li class="nobdr">
				<c:choose>
					<c:when
						test="${((applicationStatus eq '' or applicationStatus eq false) or (isActiveCeo eq 'false')) and (removeTabs ne true)}">
<!-- 						<a id="application_submit" class="submitButtonDisable"
							href="#" title="Submit the Business Application for review, once the application is complete.">Submit</a> -->
					</c:when>
					<c:when
						test="${(applicationStatus eq '' or applicationStatus eq false) or (isActiveCeo eq 'false') and removeTabs}">
<!-- 						<a id="application_submit" class="submitButtonDisable"
							href="#" title="Submit the Service Application for review, once the application is complete.">Submit</a> -->
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${removeTabs and applicationType eq 'service'}">
								<c:choose>
									<c:when test="${(deactivatedService ne null and  deactivatedService)or (isActiveCeo eq 'false')}">
<!-- 										<a id="application_submit" class="submitButtonDisable"
								href="#" title="Submit the Service Application for review, once the application is complete.">Submit</a> -->
									</c:when>
									<c:otherwise>
										<a id="application_submit" 
								href="${url}&section=applicationsubmission&subsection=applicationsubmission&next_action=applicationSubmit&applicationType=${applicationType}"
								title="Submit the Service Application for review, once the application is complete.">Submit</a>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<a id="application_submit" 
								href="${url}&section=applicationsubmission&subsection=applicationsubmission&next_action=applicationSubmit&applicationType=${applicationType}"
								title="Submit the Business Application for review, once the application is complete.">Submit</a>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
				</li>
			</ul> --%>
		<jsp:scriptlet>
			}
		</jsp:scriptlet> 
	</div>
	
	<jsp:scriptlet>
		}
	</jsp:scriptlet>
</div>
<br class="clear"/>
<script>
    var isdropDownChange = false;
	var lastDataArray = new Array();
	$(function(){
		//Shows unsaved data popup
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
				$("div[id^='errorIndElt']").each(function(){
					if($(this).find("label").size() > 0){
						isSame = false;
					}
                });
				if((!isSame && lastDataArray != null && lastDataArray.length > 0)||isdropDownChange){
					e.preventDefault();
					$('<div id="dialogBox"></div>').appendTo('body')
					.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
					.dialog({
						modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
						width: 'auto', modal: true, resizable: false, draggable:false,
					    dialogClass: 'dialogButtons',
						buttons: {
							OK: function () {
								//Start R5: UX module, clean AutoSave Data
								deleteAutoSaveData();
								//End R5: UX module, clean AutoSave Data
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
	//fetches data form save popup
	$(window).load(function(){
		var ignoreForms = ["myinboxform", "myTaskMform"];
		$("form").each(function(){
			if(typeof($(this).attr("name")) != "undefined" && $.inArray($(this).attr("name"), ignoreForms) < 0){
				lastDataArray[lastDataArray.length] = new Array($(this).attr("name"), $(this).serializeArray());
			}
		});
	});
</script>