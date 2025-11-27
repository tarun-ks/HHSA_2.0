<!-- This page will be displayed when a user select a service and complete the selection.
It display  list of selected services and user can submit the supporting information-->
<%@page import="com.nyc.hhs.model.ServiceSummaryStatus"%>
<%@ page import="java.util.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.model.ServiceSummary"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/ddaccordion.js" charset="utf-8"></script>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>


<portlet:defineObjects />
<style>
.ui-accordion .ui-accordion-content {
	display : block;
	background: transparent;
	padding: 0.5em 0;
	overflow: hidden;
	white-space: nowrap;
}
.tabularWrapper{
	height: auto;
}
H5 {
	width:630px;
}
.hyperlinkclass:hover{
	text-decoration: underline;
}
.servicesummaryLftCol{
	text-align: right;
	width: 18%
}
.servicesummaryRhtCol{
	width:82%
}
.accContainer{
	width:100% !important
}
</style>
<script>
//removes the selected service
function removeSelectedService(element, serviceAppId, actionUrl)
{
	$('<div id="dialogBox"></div>').appendTo('body')
    .html('<div>Are you sure you want to remove this Service?</div>')
    .dialog({
          modal: true, title: 'Remove Document', zIndex: 10000, autoOpen: true,
          width: 'auto', modal: true, resizable: false, draggable:false,
          buttons: {
                Ok: function () {
                	$(this).dialog("close");//Adding Fix for defect 1817, closing Overlay on Click of OK button to avoid multiple hit
                	pageGreyOut();// Adding Page gray Out for the wait time till request gets processed.
                	actionUrl = actionUrl + "&subsection=summary&service_app_id="+serviceAppId+"&next_action=removeService";
                	$(element).closest("form").attr("action", actionUrl);
                	$(element).closest("form").submit();
                },
                Cancel: function () {
             	   $(this).dialog("close");
                }
          },
          close: function (event, ui) {
                $(this).remove();
          }
    });
}
</script>

<script type="text/javascript">

	function selectAllAndSubmit(pageToDirect,id) {
	   	document.servicesummaryform.next_action.value=pageToDirect;
	   	document.servicesummaryform.element_id.value=id;
	   	document.servicesummaryform.section.value="business_application_summary";
	   	document.servicesummaryform.subsection.value="service_summary_remove";
	   	document.servicesummaryform.submit();
	}
	
	function openServicePage(actionName) {
	   	document.servicesummaryform.action = document.servicesummaryform.action+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&section=servicessummary&subsection=addservice&next_action="+actionName;
	   	document.servicesummaryform.submit();
	}
	
	//Initialize for Collapse and Expand Demo:
	ddaccordion.init({
		headerclass: "hdng", //Shared CSS class name of headers group
		contentclass: "accContainer", //Shared CSS class name of contents group
		revealtype: "click", //Reveal content when user clicks or onmouseover the header? Valid value: "click", "clickgo", or "mouseover"
		mouseoverdelay: 200, //if revealtype="mouseover", set delay in milliseconds before header expands onMouseover
		collapseprev: false, //Collapse previous content (so only one open at any time)? true/false 
		defaultexpanded: [], //index of content(s) open by default [index1, index2, etc]. [] denotes no content.
		onemustopen: false, //Specify whether at least one header should be open always (so never all headers closed)
		animatedefault: false, //Should contents open by default be animated into view?
		scrolltoheader: false, //scroll to header each time after it's been expanded by the user?
		persiststate: false, //persist state of opened contents within browser session?
		toggleclass: ["closedlanguage", "openlanguage"], //Two CSS classes to be applied to the header when it's collapsed and expanded, respectively ["class1", "class2"]
		//togglehtml: ["prefix", "<img src='http://i13.tinypic.com/80mxwlz.gif' style='width:13px; height:13px' /> ", "<img src='http://i18.tinypic.com/6tpc4td.gif' style='width:13px; height:13px' /> "], //Additional HTML added to the header when it's collapsed and expanded, respectively  ["position", "html1", "html2"] (see docs)
		animatespeed: "fast", //speed of animation: integer in milliseconds (ie: 200), or keywords "fast", "normal", or "slow"
		oninit:function(expandedindices){ //custom code to run when headers have initalized
			//do nothing
		},
		onopenclose:function(header, index, state, isuseractivated){ //custom code to run whenever a header is opened or closed
			//do nothing
		}
	});

			$(function(){
				<c:forEach var="map1" items="${loBusinessStatusBeanMap}">
					setStatusSection("${map1.key}", "${map1.value.msSectionStatus}");
				</c:forEach>
				$(".hyperlinkclass").click(function(e){
					e.stopPropagation();
					var serviceAppId = $(this).parent().parent().attr("id");
					var elementId = $(this).parent().parent().parent().attr("id");
					var sectionName = "<%=renderRequest.getAttribute("section")%>";
					var actionUrl  = $(this).closest("form").attr("action")+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&elementId="+elementId;
					if($(this).hasClass('submit')){
						actionUrl = actionUrl + "&subsection=questions&service_app_id="+serviceAppId+"&next_action=showServiceQuestion";
						$(this).closest("form").attr("action",actionUrl);
						$(this).closest("form").submit();
					}else if($(this).hasClass('remove')){
						removeSelectedService(this, serviceAppId, actionUrl);
					}else if($(this).hasClass('printer')){
						actionUrl = $("#contextPath").val()+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_add_service&removeNavigator=abcd&removeMenu=ddd&_nfls=false&section="+sectionName+"&subsection=printerFriendly&service_app_id="+serviceAppId+"&next_action=printerFriendly&business_app_id=<%=renderRequest.getAttribute("business_app_id")%>&applicationId=<%=session.getAttribute("applicationId")%>";
						window.open(actionUrl);
					}
				});
				// Accordion
				$(".accrodinWrapper").accordion();
				$("h5").disable();
				//$("h5").remove();
				$("label").click(function(event){
					event.stopPropagation();
				});
				$( ".accrodinWrapper" ).accordion({
					collapsible: true,
					autoHeight: false
				});


				// Tabs
				$('#tabs').tabs();
				$('#newTabs').tabs();
				// Dialog
				$('#dialog').dialog({
					autoOpen: false,
					width: 600,
					buttons: {
						"Ok": function() {
							$(this).dialog("close");
						},
						"Cancel": function() {
							$(this).dialog("close");
						}
					}
				});
			});
		</script>




<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S065_PAGE, request.getSession())
		// Start : R5 Added
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())
		// End : R5 Added
		){%>
	<form action="<portlet:actionURL/>" method="post" name="servicesummaryform">
	<c:if test="${deactivatedService ne null and  deactivatedService}">
		<div style="padding-top:31px">
		<div class="failed" style="display:block;margin-top:5px;" >You cannot submit your application as it is no longer required to apply for ${lsDeactivatedServices}. Please remove the services from your application.</div>
		</div>
		
	</c:if>
	<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
	<h2 >Services Summary</h2>
    <div class="overlay"></div>
	<div class="alert-box-contact">
		<div class="content">
			<div id="newTabs">
				<div id="contactDiv"></div>
			</div>
		</div>
	</div>
	
	<div class="hr"></div>
	<%-- Start : R5 Added --%>
	<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S065_SECTION, request.getSession())){%>
	<p>Listed below are the Services that your organization has selected for this application. To view related Services that your organization may be able to provide, 
	please click the "View Related Services" button. To return to the index of Services or search by Keyword for additional Services, please click the "Add Services" button.</p>
	<p>You must provide supporting documentation for each selected
	Service.</p> 
	<div class="hr"></div>
	<c:set var="isReadOnlyUser" value=""></c:set>
	<c:set var="viewServicesClass" value="graybtutton"></c:set>
	<c:if test="${servicesReadOnlyUser eq true }">
		<c:set var="isReadOnlyUser" value="disabled"/>
		<c:set var="viewServicesClass" value=""></c:set>	
	</c:if>
	<div class="buttonholder">
		<input type="button" value="View Related Services" class="${viewServicesClass}" ${isReadOnlyUser} title="Click here to review Services you may also be interested in." onclick="openServicePage('showsimilarServicesAll')" /> 
<%-- 		<input type="button" class="button" value="+ Add Services" ${isReadOnlyUser} title="Click here to return to Services Index and keyword search." onclick="openServicePage('showServices')" /> --%>
	</div>
	<%}%>
	<div class="accordion">
	<input type="hidden" name="applicationType" value="service">
	<%
		ArrayList<ServiceSummary>loSummaryList = null ; // list 
		if((renderRequest.getAttribute("SummaryServiceListOfMap") != null)){
			
			loSummaryList=(ArrayList<ServiceSummary>)renderRequest.getAttribute("SummaryServiceListOfMap");
			Iterator<ServiceSummary> loItr = loSummaryList.iterator();
			while(loItr.hasNext()){
				
				ServiceSummary loService = loItr.next();
				ServiceSummaryStatus loStatus = loService.getServiceSubSectionStatus();
				%>
				<c:set var="service_app_id" value="<%=loService.getMsServiceAppId()%>"></c:set>
				<div class="accordianStatus">
					<b>Status:</b> 
					<label class="sub-${loServicesStatusBeanMap[service_app_id].msSectionStatus}">${loServicesStatusBeanMap[service_app_id].msSectionStatusOnInnerSummary}</label>
				</div>
				<div class="clear"></div>
				<div class="accrodinWrapper hdng" id="<%=loService.getMsServiceElementId()%>">
					<h5 name="service"  value="<%=loService.getMsServiceAppId()%>"><%=loService.getMsServiceName()%></h5>
					<ul id="<%=loService.getMsServiceAppId()%>">
					<c:choose>
						<c:when test="${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['questions'] eq 'notstarted' and loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['documentlist'] eq 'notstarted' and loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['specialization'] eq 'notstarted' and loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['servicesetting'] eq 'notstarted' }">
							<li ><label class="submit hyperlinkclass">Add Supporting Information</label></li>
						</c:when>
						<c:otherwise>
							<li ><label class="submit hyperlinkclass">Edit Supporting Information</label></li>
						</c:otherwise>
					</c:choose>
						<li><label class="printer hyperlinkclass">View Printer Friendly Version</label></li>
					<c:if test="${servicesReadOnlyUser ne true }">
						<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S065_SECTION, request.getSession())){%>
						<li><label class="remove hyperlinkclass">Remove Service</label></li>
						<%} %>
						</c:if>
					</ul>
					<%-- End : R5 Added --%>
				</div>
				<div class="accContainer">
				<!-- Grid Starts -->
					<div class="tabularWrapper clear">
						<table cellspacing="0" cellpadding="0" class="grid">
							<tr>
								<td class='servicesummaryLftCol'><a href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="questions" /><portlet:param name="next_action" value="showServiceQuestion" /><portlet:param name="elementId" value="<%=loService.getMsServiceElementId()%>" /></portlet:renderURL>" title="Service Question">Service Questions: </a></td>
								<td class='servicesummaryRhtCol'><span class="accordianStatus"> <label
									class="sub-${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['questions']}">${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetailsToDisplay['questions']}</label> </span></td>
							</tr>
							<tr class="alternate">
								<td class='servicesummaryLftCol'><a href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="documentlist"/><portlet:param name="next_action" value="open" /><portlet:param name="elementId" value="<%=loService.getMsServiceElementId()%>" /></portlet:renderURL>" title="Service Document">Service Document: </a></td>
								<td class='servicesummaryRhtCol'><span class="accordianStatus"> <label
									class="sub-${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['documentlist']}">${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetailsToDisplay['documentlist']}</label> </span></td>
							</tr>
							<tr>
								<td class='servicesummaryLftCol'><a href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="specialization"/><portlet:param name="next_action" value="open" /><portlet:param name="elementId" value="<%=loService.getMsServiceElementId()%>"/></portlet:renderURL>" title="Specialization">Specialization: </a></td>
								<td class='servicesummaryRhtCol'><span class="accordianStatus"> 
										<label class="sub-${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['specialization']}">${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetailsToDisplay['specialization']}</label> 
									</span>
								</td>
							</tr>
							<tr class="alternate">
								<td class='servicesummaryLftCol'><a href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="servicesetting"/><portlet:param name="next_action" value="open" /><portlet:param name="elementId" value="<%=loService.getMsServiceElementId()%>"/></portlet:renderURL>" title="Service Setting">Service Setting: </a></td>
								<td class='servicesummaryRhtCol'><span class="accordianStatus"> <label
									class="sub-${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['servicesetting']}">${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetailsToDisplay['servicesetting']}</label> </span></td>
							</tr>
						</table>
					</div>
				</div>
				
		  <%
		  }
		
     	  }
 		 %> 
	<script type="text/javascript">
		showSelected('${section }','${subsection }');
	</script>
	</div>
	<% } else {%>
	   <h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
    <%} %> 