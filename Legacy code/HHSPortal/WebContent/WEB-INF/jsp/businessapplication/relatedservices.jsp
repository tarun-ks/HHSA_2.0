<!-- This page will be displayed when a user click on the view related service button.
It display  list of related services that can be added.-->
<%@page import="org.jdom.Document"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb"%>
<%@page import="com.nyc.hhs.util.BusinessApplicationUtil"%>
<%@page import="com.nyc.hhs.model.TaxonomyServiceBean"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%><portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/ddaccordion.js"></script>
<script type="text/javascript" src="../resources/js/relatedServices.js"></script>
<style>
.addTaxonomyButtons{
   	background: url("../images/trans_bg.png") repeat-x scroll 0 0 #26B83F;
    border: 1px solid #1DA52D;
    color: #FFFFFF;
    cursor: pointer;
    font-family: verdana,sans-serif;
    font-size: 11px;
    font-weight: bold;
    margin-left: 2px;
    padding: 5px;
    width:80px;
}
.removeTaxonomyButtons{
	background: url("../images/trans_bg.png") repeat-x scroll 0 0 #FF0000 !important;
    border: 1px solid #B92801 !important;
    color: #FFFFFF;
    cursor: pointer;
    font-family: verdana,sans-serif;
    font-size: 11px;
    font-weight: bold;
    margin-left: 2px;
    padding: 5px;
    width:80px;
}
.displayNone{
		display:none;
	}
</style>
<script type="text/javascript">
//called on load of page sets tabs
	$(function(){
		showSelected('${section }','${subsection }');
		<c:forEach var="map1" items="${loBusinessStatusBeanMap}">
			setStatusSection("${map1.key}", "${map1.value.msSectionStatus}");
		</c:forEach>
		$("#removeAll").click(function(){
			var visibleLen = $("#selected_Services li:visible");
			visibleLen.each(function(i){
				var liId = $(this).attr("id");
				var liText = $("#"+liId).html();
				//liId = liId.substring(14,$(this).attr("id").length);
				var inputId = "myButton"+liId;
			 	addRemoveService(liText,document.getElementById(inputId),liId);
			});
		 });
	});
</script>
<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S075_PAGE, request.getSession())){%>
	<form method="post" action="<portlet:actionURL/>" name="servicesummaryform">

	<div class="overlay"></div>
	
	<div class="alert-box-contact">
				<div class="content">
					<div id="newTabs">
						<div id="contactDiv"></div>
					</div>
				</div>
	</div>
	<h3 style="display:inline">Related Services(OPTIONAL)</h3>
	<div class="floatRht" style="margin-left: 10px; margin-top: 8px;"><a id="returnSummaryPage" title="Service Summary"  class="returnButton"  href="<portlet:renderURL><portlet:param name='business_app_id' value='${business_app_id}'/><portlet:param name='elementId' value='${elementId}' /><portlet:param name='section' value='${section}'/><portlet:param name='service_app_id' value='${service_app_id}'/><portlet:param name='subsection' value='summary'/><portlet:param name='next_action' value='checkForService'/><portlet:param name='displayHistory' value='displayHistory'/></portlet:renderURL>">Service Summary</a></div>
    <p>The list below displays Services related to those selected by your organization. 
    Please review each one to see if your organization also has the ability and resources to provide that Service. 
    Use the "Add" button to indicate your organization can provide that Service</p>
    <div class='evenRows floatRht' style='margin-top:-11px;'>
			<a href="javascript:;" id="removeAll" title='Remove All' class="displayNone"><b>Remove All</b></a>
		</div>
	<div class="selectedContainer">
		<h4>Selected Services</h4>
		<ul id="selected_Services" >
				<li class="noneSelected" id="noneSelected">None selected...</li>
		</ul>
	<!-- Selected Container end -->
	</div>
	<div class="tabularWrapper">
		<table >
			<tr>
				<th style="text-align:right; padding-right:10px;">Service</th>
				<th colspan="2">Description</th>
			</tr>
			<%	List<TaxonomyTree> loLRelatedServices = null;
			if(request.getAttribute("loRelatedServicesList")!=null ){
				loLRelatedServices = (List<TaxonomyTree>) request.getAttribute("loRelatedServicesList");
			}
				Iterator<TaxonomyTree> loItr = loLRelatedServices.iterator();
				 while(loItr.hasNext()){
					TaxonomyTree loService = loItr.next();
		%>
			<tr>
				<td width="100px" style="text-align:right; padding-right:10px; border-bottom: 1px dotted;"><b><%=loService.getMsDisplayName()%></b></td>
				<td width="680px" style="text-align:left; border-bottom: 1px dotted;"><%=loService.getMsElementDescription()%></td>
				<td width="100px" style="border-bottom: 1px dotted;">
					<input id="myButton<%=loService.getMsElementid()%>" type="button" class="addTaxonomyButtons" style="float:right;" value="+ Add" title="+ Add" onclick="addRemoveService('<%=loService.getMsDisplayName()%>',this,'<%=loService.getMsElementid()%>');"/>
				</td>
			</tr>
		<%}%>
		</table>
		<c:if test="${fn:length(loRelatedServicesList) eq 0}">
			<div class="floatLft" style="margin-top: 8px;"> 
				<b>There are no related services for the selected services. Click Complete Additions to continue</b>
			</div>
			<div class='clear'></div>
		</c:if>
	</div>
	<div class="hr"></div>
	<div class="buttonholder">
		<input type="submit" class="graybtutton" value="Cancel" title="Cancel" onclick="setValue('back', '<%=renderRequest.getAttribute("business_app_id")%>')"/>
		<input name="saveService" type="button" class="button" value="Complete Additions" title="Complete Additions" onclick="setValue('saveRelatedServices','<%=renderRequest.getAttribute("business_app_id")%>')"/>
		<input type="hidden" name="selectedService" value="" id="addSelectedServices">
		<input type="hidden" name="submitButtonValue" value="" id="saveServices">
		<input type="hidden" name="subsection" value="" id="addservice">
	</div>

	</form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>