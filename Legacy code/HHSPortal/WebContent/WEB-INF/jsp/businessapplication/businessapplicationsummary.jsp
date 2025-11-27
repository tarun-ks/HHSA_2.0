<!-- This page is displayed when a user click on  the Business Application Summary  tab.
It will display Business Application Summary  for basic, board, filings, policies form.-->
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.BusinessApplicationSummary"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>

<portlet:defineObjects />

<style>
.ui-accordion .ui-accordion-content {
	background: transparent;
	padding: 0.8em 0;
	overflow: hidden;
	white-space: nowrap;
	display:block;
}
.tabularWrapper{
	height: auto;
}
.iconComments:hover	{
	text-decoration:underline;
}
.commentBox{
	display:none;
}
.accContainer{
	width:100% !important
}
</style>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/resources/js/ddaccordion.js" charset="utf-8"></script>
<script type="text/javascript">
//This method will disable expansion of Accordions in case no data exists for section 
function hideMe (id) {
	document.getElementById(id).style.visibility = 'hidden';
	document.getElementById(id).style.height = '0px'; 
} 
function onloadMethod(){
<%			List<BusinessApplicationSummary> loBasicSummaryList = null;
			List<BusinessApplicationSummary> loFilingSummaryList = null;
			List<BusinessApplicationSummary> loBoardSummaryList = null;
			List<BusinessApplicationSummary> loPoliciesSummaryList = null;
			if(null != request.getAttribute("loBasicSummaryList"))
			loBasicSummaryList = (List<BusinessApplicationSummary>) request.getAttribute("loBasicSummaryList");
			if(null != request.getAttribute("loBasicSummaryList"))
			loFilingSummaryList = (List<BusinessApplicationSummary>) request.getAttribute("loFilingSummaryList");
			if(null != request.getAttribute("loBasicSummaryList"))
			loBoardSummaryList = (List<BusinessApplicationSummary>) request.getAttribute("loBoardSummaryList");
			if(null != request.getAttribute("loBasicSummaryList"))
			loPoliciesSummaryList = (List<BusinessApplicationSummary>) request.getAttribute("loPoliciesSummaryList");

			//Control to disable expansion of Accordions in case no data exists for section
			if (null == loBasicSummaryList || loBasicSummaryList.isEmpty()) {%>
				hideMe('basicSectionSummary');
<%			}
			if (null == loBoardSummaryList || loBoardSummaryList.isEmpty()) {%>
				hideMe('boardSectionSummary');
<%			}
			if (null == loFilingSummaryList || loFilingSummaryList.isEmpty()) {%>
				hideMe('filingsSectionSummary');
<%			}
			if (null == loPoliciesSummaryList || loPoliciesSummaryList.isEmpty()) {%>
				hideMe('policiesSectionSummary');
<%			}
%>
<c:if test="${loBusinessStatusBeanMap['basics'].msSectionStatus eq 'notstarted'}">
	hideMe('basicSectionSummary');				
</c:if>
			

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
$(document).ready(function(){
	onloadMethod();
	// Accordion
		$(".accrodinWrapper").accordion();
		$(".iconComments").click(function(event){
			event.stopPropagation();
			var toShowSection = "";
			if($(this).hasClass('basicsComment')){
				toShowSection = 'basics';
			}else if($(this).hasClass('filingsComment')){
				toShowSection = 'filings';
			}else if($(this).hasClass('boardComment')){
				toShowSection = 'board';
			}else if($(this).hasClass('policiesComment')){
				toShowSection = 'policies';
			}
			if(toShowSection.length > 0){
				$("#"+toShowSection+"_comments").addClass("alert-box").find(".commentHeading").addClass("tabularCustomHead");
				if($("#"+toShowSection+"_comments").find(".exit-panel").size() == 0)
					$("#"+toShowSection+"_comments").find(".commentHeading").append("<a href='javascript:void(0);' class='exit-panel'>&nbsp;</a>");
				$(".overlay").launchOverlay($("#"+toShowSection+"_comments"), $(".exit-panel"), "200px", null, null);
			}
		});
		$("h5").disable();
		$( ".accrodinWrapper" ).accordion({
			collapsible: true,
			autoHeight: false
		});
});
function openSummaryLink(sectionName, subSectionName, nextAction){
	var hiddenBusinessAppId = $("#hiddenBusinessAppId").val();
    var hiddenBusinessAppStatus = $("#hiddenBusinessAppStatus").val();
    location.href = $("#contextPath").val()+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_section&_nfls=false&bussAppStatus="+hiddenBusinessAppStatus+"&business_app_id="+hiddenBusinessAppId+"&section="+sectionName+"&subsection="+subSectionName+"&next_action="+nextAction+"#wlp_portlet_hhsweb_portal_page_section";
}
</script>
<c:set var="urlApplication"
		value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_add_service&_nfls=false&bussAppStatus=${bussAppStatus}&business_app_id=${business_app_id}&applicationId=${applicationId}"></c:set>
<title>NYC_Human Health Services Accelerator</title>
</head>
<body>
<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S060_PAGE, request.getSession())
		// Start : R5 Added
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())
		// End : R5 Added
		){%>
<form name="populationform" action='<portlet:actionURL/>' method="post">
<input type="hidden" name="next_action" value="" /> <!-- Body Wrapper Start -->
<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />


<h2>Business Application Summary <label class="linkPrint">
<a target="_blank" title="View Printer Friendly Version" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_section&removeNavigator=abcd&removeMenu=ddd&_nfls=false&section=<%=ApplicationConstants.BUSINESS_APPLICATION_SECTION_REVIEW_SUMMARY%>&subsection=<%=ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_APP_PRINTER_FRIENDLY%>&business_app_id=<%=renderRequest.getAttribute("business_app_id")%>">View Printer Friendly Version</a> </label></h2>
<div class="hr"></div>
<div>Use this page to review the summaries of the statuses in the
previous four sections (Basics, Filings, Board, Policies). <br />
Click on each section to view the status details.</div>


<div class="accordion">
<div class="accordianStatus"><b>Status</b>:<label
	class="sub-${loBusinessStatusBeanMap['basics'].msSectionStatus}">${loBusinessStatusBeanMap['basics'].msSectionStatusOnInnerSummary}</label></div>
	${aoBusinessSummaryMap['basics']}
	<c:if test="${loBusinessStatusBeanMap['basics'].msSectionStatusOnInnerSummary eq 'Not Started'}">
		<c:set var="basicAccord" value="noExpand"></c:set>
	</c:if>
<div class="${basicAccord} hdng accrodinWrapper">
	<h5>Basics</h5>
	<!-- Start : R5 Added -->
	<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S060_PAGE, request.getSession())){%>
	<c:if test="${! empty aoBusinessSummaryMap['basics']}">
		<ul>
			<li><label class="iconComments basicsComment">Show Basics Comments</label></li>
		</ul>
	</c:if>
	<%}%>
	<!-- End : R5 Added -->
</div>
<div id="basicSectionSummary" class="accContainer">
<!-- Grid Starts -->
<div class="tabularWrapper clear"><st:table
	objectName="loBasicSummaryList" cssClass="heading" 
	alternateCss1="evenRows" alternateCss2="oddRows">
	<st:property headingName="Questions/Document Name"
		columnName="msQuestionsDocumentName" size="30%" align="center">
		<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameExtension" />
	</st:property>
	<st:property headingName="Document Type" columnName="msDocumentType"
		size="30%" align="center">
	</st:property>
	<st:property headingName="Status" columnName="msStatus" size="15%"
		align="center">
	</st:property>
	<st:property headingName="Modified" columnName="msModifiedDate"
		size="10%" align="center">
		<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameDateExtension" />
	</st:property>
	<st:property headingName="Last Modified By" columnName="msModifiedBy"
		size="15%" align="center">
		<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameModByExtension" />
	</st:property>
</st:table></div>
</div>
<!-- Grid Ends --> 

<!--xxxxxxxxxxxxxxxxxxxxxxxxxxxxx Filings Summary xxxxxxxxxxxxxxxxxxxxxxxxxxx-->
<div class="accordianStatus"><b>Status</b>:<label
	class="sub-${loBusinessStatusBeanMap['filings'].msSectionStatus}">${loBusinessStatusBeanMap['filings'].msSectionStatusOnInnerSummary}</label></div>
	${aoBusinessSummaryMap['filings']}
	<c:if test="${loBusinessStatusBeanMap['filings'].msSectionStatusOnInnerSummary eq 'Not Started'}">	
		<c:set var="filingAccord" value="noExpand"></c:set>
	</c:if>
	<div class="${filingAccord} hdng accrodinWrapper">	
		<h5>Filings</h5>
		<!-- Start : R5 Added -->
		<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S060_PAGE, request.getSession())){%>
		<c:if test="${! empty aoBusinessSummaryMap['filings']}">
			<ul>
				<li><label class="iconComments filingsComment">Show Filings Comments</label></li>
			</ul>
		</c:if>
		<%}%>
		<!-- End : R5 Added -->
	</div>
<!-- Grid Starts -->
<div id="filingsSectionSummary" class="accContainer">
<div class="tabularWrapper clear"><st:table 
	objectName="loFilingSummaryList" cssClass="heading"
	alternateCss1="evenRows" alternateCss2="oddRows" >
	<st:property headingName="Questions/Document Name"
		columnName="msQuestionsDocumentName" size="30%" align="center">
		<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameExtension" />
	</st:property>
	<st:property headingName="Document Type" columnName="msDocumentType"
		size="30%" align="center">
	</st:property>
	<st:property headingName="Status" columnName="msStatus" size="15%"
		align="center">
	</st:property>
	<st:property headingName="Modified" columnName="msModifiedDate"
		size="10%" align="center">
	<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameDateExtension" />
	</st:property>
	<st:property headingName="Last Modified By" columnName="msModifiedBy"
		size="15%" align="center">
		<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameModByExtension" />
	</st:property>
</st:table></div>
</div>
<!-- Grid Ends --> 

<!--xxxxxxxxxxxxxxxxxxxxxxxxxxxxx Board Summary xxxxxxxxxxxxxxxxxxxxxxxxxxx-->
<div class="accordianStatus"><b>Status</b>:<label
	class="sub-${loBusinessStatusBeanMap['board'].msSectionStatus}">${loBusinessStatusBeanMap['board'].msSectionStatusOnInnerSummary}</label></div>
	${aoBusinessSummaryMap['board']}
	<c:if test="${loBusinessStatusBeanMap['board'].msSectionStatusOnInnerSummary eq 'Not Started'}">
		<c:set var="boardAccord" value="noExpand"></c:set>
	</c:if>
<div class="accrodinWrapper hdng ${boardAccord }">
	<h5>Board</h5>
		<!-- Start : R5 Added -->
		<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S060_PAGE, request.getSession())){%>
		<c:if test="${! empty aoBusinessSummaryMap['board']}">
			<ul>
				<li><label class="iconComments boardComment">Show Board Comments</label></li>
			</ul>
		</c:if>
		<%}%>
		<!-- End : R5 Added -->
</div>
<!-- Grid Starts -->
<div id="boardSectionSummary" class="accContainer">
<div class="tabularWrapper clear"><st:table 
	objectName="loBoardSummaryList" cssClass="heading"
	alternateCss1="evenRows" alternateCss2="oddRows" >
	<st:property headingName="Questions/Document Name"
		columnName="msQuestionsDocumentName" size="30%" align="center">
		<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameExtension" />
	</st:property>
	<st:property headingName="Document Type" columnName="msDocumentType"
		align="center" size="30%">
	</st:property>
	<st:property headingName="Status" columnName="msStatus" size="15%"
		align="center">
	</st:property>
	<st:property headingName="Modified" columnName="msModifiedDate"
		size="10%" align="center">
	<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameDateExtension" />
	</st:property>
	<st:property headingName="Last Modified By" columnName="msModifiedBy"
		size="15%" align="center">
		<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameModByExtension" />
	</st:property>
</st:table></div>
</div>
<!-- Grid Ends --> <!--xxxxxxxxxxxxxxxxxxxxxxxxxxxxx Policies Sumarry xxxxxxxxxxxxxxxxxxxxxxxxxxx-->
<div class="accordianStatus"><b>Status</b>:<label
	class="sub-${loBusinessStatusBeanMap['policies'].msSectionStatus}">${loBusinessStatusBeanMap['policies'].msSectionStatusOnInnerSummary}</label></div>
	${aoBusinessSummaryMap['policies']}
	<c:if test="${loBusinessStatusBeanMap['policies'].msSectionStatusOnInnerSummary eq 'Not Started'}">
		<c:set var="policiesAccord" value="noExpand"></c:set>
	</c:if>	
<div class="accrodinWrapper hdng ${policiesAccord} }">
	<h5>Policies</h5>
	<!-- Start : R5 Added -->
	<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S060_PAGE, request.getSession())){%>
	<c:if test="${! empty aoBusinessSummaryMap['policies']}">
		<ul>
			<li><label class="iconComments policiesComment">Show Policies Comments</label></li>
		</ul>
	</c:if>
	<%}%>
	<!-- End : R5 Added -->
</div>
<!-- Grid Starts -->
<div id="policiesSectionSummary" class="accContainer">
<div class="tabularWrapper clear"><st:table 
	objectName="loPoliciesSummaryList" cssClass="heading"
	alternateCss1="evenRows" alternateCss2="oddRows" >
	<st:property headingName="Questions/Document Name"
		columnName="msQuestionsDocumentName" size="30%" align="center">
		<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameExtension" />
	</st:property>
	<st:property headingName="Document Type" columnName="msDocumentType"
		size="30%" align="center">
	</st:property>
	<st:property headingName="Status" columnName="msStatus" size="15%"
		align="center">
	</st:property>
	<st:property headingName="Modified" columnName="msModifiedDate"
		size="10%" align="center">
	<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameDateExtension" />
	</st:property>
	<st:property headingName="Last Modified By" columnName="msModifiedBy"
		size="15%" align="center">
		<st:extension
			decoratorClass="com.nyc.hhs.frameworks.grid.BassicApplicationSummaryNameModByExtension" />
	</st:property>
</st:table></div>
<input type="hidden" value="${business_app_id}" id="hiddenBusinessAppId"/>
<input type="hidden" value="${bussAppStatus}" id="hiddenBusinessAppStatus"/>
</div>
<!-- Grid Ends -->
<c:if test="${loReadOnlyStatus eq 'draft' or loReadOnlyStatus eq 'Draft'}">
	<div class="buttonholder">
		<br /><a id="Next" class="button"	href="${urlApplication}&section=servicessummary&subsection=summary&next_action=checkForService" title="Next">Next >></a>
	</div>
</c:if>
</div>
<!-- Body Container Ends --> <!-- Body Wrapper End --></form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>
