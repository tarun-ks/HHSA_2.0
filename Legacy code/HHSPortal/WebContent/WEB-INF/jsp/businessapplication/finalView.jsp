<!-- This page is displayed when an application is approved and user click on the business application link.
It will display printer friendly detail of application-->
<%@ page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.util.PortalUtil"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<link href="${pageContext.servletContext.contextPath}/resources/css/finalPrint.css" rel="stylesheet" type="text/css" media="print"></link>
<style type="text/css">
.commentBox{
	display:none;
}
.tableHeaderPrint{
	 background-color: #E4E4E4;
}
.tableRowEvenPrint{
	background-color: #FFFFFF;
}
.tableRowOddPrint{
 	background-color: #F2F2F2;
}
.content{
	padding-top: 5px;
}
.content td{
	width: 44%;
	padding: 4px 4px 0 10px;
	vertical-align: top;
	
}
.content table{
	width: 80%;
}
.content table.documentTablePrint{
	width: 100%;
}

.content table.documentTablePrint td{
	width: auto;
	padding:6px
}
.content table.documentTablePrint a{
	color: blue;
	text-decoration: underline;
}
.content th{
	font-weight: bold;
	padding-left: 10px;
}
.noContent{
	border:1px solid grey;
	padding: 5px 0 5px 10px;
}
table{
	padding-bottom:20px;
}
.questions{
    text-align: right;
	background: none repeat scroll 0 0 #F2F2F2;
    margin: 0 8px 3px 0;
    border-top: 2px solid #ffffff;
}
.headingFont{
	font-wight:bold;
	font-size: 18px;
}
.sectionHeading{
	font-size: 16px;
}
.bodycontainer{
 	background: none repeat scroll 0 0 #FFFFFF;
    padding: 0px;
    width: auto;
}
br{
	display:none
}
td br{
	display: block;
}
.print-heading{
	background: none;
	font-size:17px;
	font-weight:bold;
	text-decoration:underline;
}
.headingText{
	border-bottom: 2px solid black;
    margin-bottom: 3px;
    padding-top: 7px;
}
.subheading{
	font-weight: bold;
}
.commentLinkClass{
	float:right;
	color:blue;
	text-decoration:underline;
	cursor: pointer;
}
.iconQuestion {
	margin-top: 14px !important;
	*margin-top: 8px !important;
}
.print_header{
	background-color: #F8F8F8;
    border-bottom: 6px solid #1569B2;
    border-top: 2px solid #FFF;
    height: 74px;
    width: 100%;
	clear:both
}
.content .header{
	font-weight: bold;
	padding: 0px;
}
.printerFriendlyPhone{
	 background: none
}
</style>
<script>
//Ready event.
	$(document).ready(function(){
		$(".questions").each(function(){
			$(this).height($(this).parent().height());
		});
		var totalHeight = $(".finalViewContentDiv").height();
		$(".container").css("min-height", totalHeight);
		$(".commentLinkClass").click(function(){
			var toShowSection = $(this).attr("toshow");
			$("#"+toShowSection+"_comments").addClass("alert-box").find(".commentHeading").addClass("tabularCustomHead");
			if($("#"+toShowSection+"_comments").find(".exit-panel").size() == 0)
				$("#"+toShowSection+"_comments").find(".commentHeading").append("<a href='javascript:void(0);' class='exit-panel'>&nbsp;</a>");
			$(".overlay").launchOverlay($("#"+toShowSection+"_comments"), $(".exit-panel"), "200px", null, null);
		});
		$("a[name='docInfo']").click(function(){
			var eltClickedDocId = $(this).attr("docid");
			var eltClickedDocType = $(this).attr("doctype");
			var eltClickedDocCat = $(this).attr("doccat");
			var jqxhr = $.post("<portlet:actionURL/>&finalView=info",{documentId:eltClickedDocId})
	                 .success(function() {
						// comes here on successful response
	                    responsetext = jqxhr.responseText;
	                    var $response=$(responsetext);
	                    var data = $response.contents().find("#overlaycontent");
	                    if(data != null || data != ''){
                    	    $(".infoContent").html(data.detach());
                            var overlayLaunchedTemp = overlayLaunched;
							var alertboxLaunchedTemp = alertboxLaunched;
							overlayLaunched = overlayLaunchedTemp;
							alertboxLaunched = alertboxLaunchedTemp;
							$(".overlay").launchOverlay($(".alert-box-info"), $(".exit-panel"), "425px", null, null);
	                    }
						removePageGreyOut();
	                })
	                .error(function() {
						removePageGreyOut();
	                    return false;
	                 });
		});
	});
</script>

<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S062_PAGE, request.getSession())
		// Start : R5 Added 
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())
		// End : R5 Added 
		){%>
<!-- Body Wrapper Start -->
	<c:if test="${content.data ne null and content.data ne ''}">
	<!-- HHS Header Start -->
		<c:choose>
			<c:when test="${content.SUPER_STATUS eq null or content.SUPER_STATUS eq 'null'}">
				<c:set var="status" value="${content.APPLICATION_STATUS}"></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="status" value="${content.SUPER_STATUS}"></c:set>
			</c:otherwise>
		</c:choose>
		<div class="finalViewContentDiv">
			<div class="print_header">
				<table width="100%">
					<tr>
						<td width="20%">
							<table>
								<tr>
									<td><img src="../framework/skins/hhsa/images/HHS_logo_gray.png" /></td>
								</tr>
							</table>
						</td>
						<td width="65%">
							<table>
								<tr>
								<c:choose>
									<c:when test="${applicationType ne null && applicationType eq 'service' }">
										<td colspan="2" class="print-heading">HHS Accelerator Service Application</td>
									</c:when>
									<c:otherwise>
										<td colspan="2" class="print-heading">HHS Accelerator Business Application</td>
									</c:otherwise>
								</c:choose>
								</tr>
								<tr>
									<td class="print-td" width="300px"><b>Organization: </b>${content.ORGANIZATION_LEGAL_NAME}</td>
									<td class="print-td" width="300px"><b>Submitted by: </b>${content.MODIFIED_BY}</td>
								</tr>
								<tr>
									<td class="print-td capitalize"><b>Status: </b>${status}</td>
									<td class="print-td"><b>${status} On: </b>${content.CITY_STATUS_SET_DATE}</td>
								</tr>
							</table>
						</td>
						<td width="15%" align="right" style="text-align: right; vertical-align: top; padding-right:12px;">
							<table class='floatRht'>
								<tr>
									<td>
										<label class="linkPrint" style="margin:10px 8px 0 0;">
											<a title="Print" href="javascript:window.print();">Print</a>
										</label>
									</td>
									<td>
										<label class="iconQuestion floatRht" style="margin: 0">
											<a onclick="pageSpecificHelp('Applications');" title="Need Help?" href="javascript:void(0);"></a>
										</label>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
		<!-- HHS Header End -->
		<div class="content wordWrap">
		 <%-- Start : R5 Added --%>
		 <% if(!CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S062_PAGE, request.getSession())){ %>
			<style type='text/css'>
		     .commentLinkClass{ display: none;}
		  	</style>
		<%} %>
		<%-- End : R5 Added --%>
			${content.data}
	</div>
	</div>
	<div class="overlay"></div>
		<div class="alert-box alert-box-info">
  		<div id="newTabs" class='wizardTabs'>
  			<div class='tabularCustomHead'>Document Info
  				<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
  			</div>	
			<div class="infoContent"></div>
		</div>
	</div>
	</c:if>
	<c:if test="${content.data eq null or content.data eq ''}">
		Final view hasn't been generated at the moment. Please contact administrator for further assistance.
	</c:if>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>
