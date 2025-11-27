<!-- This page will be displayed when a user select a single service and complete the selection.
It display  list of selected services and user can submit the supporting information-->
<%@page import="com.nyc.hhs.model.ServiceSummaryStatus"%>
<%@ page import="java.util.*"%>
<%@ page import="com.nyc.hhs.model.ServiceSummary"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/ddaccordion.js" charset="utf-8"></script>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<portlet:defineObjects />
<style type="text/css">
    .commentHidden{
		display:none;
	}
	.accContainer{
		width:100% !important
	}
</style>

<script type="text/javascript">
//initializes the accordion
$(document).ready(function(){
		$(".accrodinWrapper").accordion();
});



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
// performs functions on load
			$(function(){
				$('#overlayLink').click(function() {    
					 $("#displayshared").append($(".commentHidden").show().remove());
					 $(".overlay").launchOverlay($(".alert-box-comments"), $("a.exit-panel"), "400px");
					 return false;		
				});	
				$(".hyperlinkclass").click(function(e){
					e.stopPropagation();
					var serviceAppId = $(this).parent().parent().attr("id");
					var elementId = $(this).parent().parent().parent().attr("id");
					var sectionName = "<%=renderRequest.getAttribute("section")%>";
					var actionUrl  = $(this).closest("form").attr("action")+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&elementId="+elementId;
					if($(this).hasClass('showcomments')){
						event.stopPropagation();
						var toShowSection = serviceAppId;
						if(toShowSection.length > 0){
							$("#"+toShowSection+"_comments").addClass("alert-box").find(".commentHeading").addClass("tabularCustomHead");
							if($("#"+toShowSection+"_comments").find(".exit-panel").size() == 0)
								$("#"+toShowSection+"_comments").find(".commentHeading").html("Service Comments");
								$("#"+toShowSection+"_comments").find(".commentHeading").append("<a href='javascript:void(0);' class='exit-panel'>&nbsp;</a>");
								$(".overlay").launchOverlay($("#"+toShowSection+"_comments"), $(".exit-panel"), "200px", null, null);
						}
						}else if($(this).hasClass('linkPrint')){
							actionUrl =  $("#contextPath").val()+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_service_post&removeNavigator=abcd&removeMenu=ddd&_nfls=false&section="+sectionName+"&subsection=printerFriendly&service_app_id=${service_app_id}&next_action=printerFriendly&business_app_id=<%=renderRequest.getAttribute("business_app_id")%>&applicationId=<%=session.getAttribute("applicationId")%>";
							window.open(actionUrl);
						}
					});
					// Accordion
					$(".accrodinWrapper").accordion();
					$("h5").disable();
					$("h5").remove();
					$("a").click(function(event){
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



<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S066_PAGE, request.getSession())
		// Start : R5 Added
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())
		// End : R5 Added
		){%>
	<form action="<portlet:actionURL/>" method="post" name="servicesummaryform">
	<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
	<!-- Body Container Starts -->



	<h2 style="display: inline">Service Summary<label class="linkPrint hyperlinkclass">
		<a href="javascript:;" title="View Printer Friendly Version">View Printer Friendly Version</a> </label>
	</h2>
	<div class='floatRht'>
	<c:if test="${org_type eq 'provider_org'}">
					<c:if test="${serviceComments ne null and ! empty serviceComments}">
							<%@include file="showServiceCommentsLink.jsp" %>
							 <div class="commentHidden" style="padding:10px;">
								<c:forEach var="loopItems" items="${serviceComments}" varStatus="counter">
								     	<c:if test="${counter.index ne 0}">
								     	 -------------------------------------------------<br>
								     	</c:if>
							     <b>${loopItems['USER_ID']} - <fmt:formatDate pattern="MM/dd/yyyy" value="${loopItems['AUDIT_DATE']}" /></b><br>
							      ${loopItems['DATA']}	<br>
						     </c:forEach>
					     </div>
					</c:if>
					</c:if>
				</div>
	<div class='clear'></div>		
	<div class="hr"></div>
	<div class="accordion">
	<%
		ServiceSummaryStatus loSummary = null ; // list 
		if((renderRequest.getAttribute("ServiceSummary") != null)){
			
			loSummary = (ServiceSummaryStatus)renderRequest.getAttribute("ServiceSummary");
	%>
	<div class="clear"></div>
	${aoServiceSummaryMap[service_app_id]}
	<div class="accrodinWrapper hdng" >
		<h5 name="service"  ><%=loSummary.getDocumentStatus()%></h5>
		<ul id="${service_app_id}">
			<c:if test="${! empty aoServiceSummaryMap[service_app_id]}">
				<li ><label class="hyperlinkclass showcomments">Show Comments</label></li>
			</c:if>
		</ul>
	</div>
	<div class="accContainer">
	<!-- Grid Starts -->
		<div class="tabularWrapper clear">
			<table cellspacing="0" cellpadding="0" class="grid">
				<tr>
					<td><a id="subsection_specialization" title="Specialization" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="specialization"/><portlet:param name="next_action" value="open" /></portlet:renderURL>">Specialization:</a>
					</td>
					<td>
					<span class="accordianStatus"> 
						<label ><%=loSummary.getSelectedSpecizationNames() %></label> 
					</span>
					</td>
				</tr>
				<tr class="alternate">
					<td><a id="subsection_servicesetting" title="Service Setting" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="servicesetting"/><portlet:param name="next_action" value="open" /></portlet:renderURL>">Service Setting:</a></td>
					<td><span class="accordianStatus"> <label><%=loSummary.getSelectedSettigNames() %></label> </span></td>
				</tr>
			</table>
		</div>
	</div>
				
	<%
     }
 	%> 
	<script type="text/javascript">
		showSelected('${section }','${subsection }');
	</script>
	</div>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>	