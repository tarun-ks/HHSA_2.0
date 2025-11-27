<!-- This page is displayed when a user click on save and next button on  service documents screen.
It will display specializations which best describe the unique services offered by your organization.-->
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="org.jdom.Document"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<style type="text/css">
.commentHidden {display:none}
.liTreeClass .descriptionTreeClass {
	min-height:20px; 
	border-top:0;
	postion:relative;
	padding-right:35px;
	
}
.liTreeClass .descriptionTreeClass {
	*float: none !important
}
.liTreeClass .titleClass {
	color: #5077AA;
    font-size: 13px;
    font-weight: bold;
    line-height: 18px;
    padding: 5px 5px;
    border-bottom:0;
 }
.inputClass{
 	float: right;
    position: absolute;
    right: 10px;
    top: 5px;
}
h2 {width: 96% !important}
.iconQuestion {	margin-left: 6px !important}
</style>
<script type="text/javascript">
//groups checkboxes
	groupClass = "inputClass";
	otherClass = "noSpecialization";
	groupCSS = "." + groupClass;
	otherCSS = "." + otherClass;
	var lastData = null;
	$(function(){
		var $form = $("#disableCheckBoxex").closest('form');
		lastData = $form.serializeArray();
	});
	$(document).ready(function() {
		<c:forEach var="test" items="${loTaxonomyIdList}"  varStatus="status">
			<c:forEach var="map1" items="${test}"  varStatus="status">
				var id = "${map1.value}";
				if(id!=null && id!="" && id!=true && id!='Y'){
					$("#input" + id).attr('checked', true);
					enableDisableCheckBox($("#input" + id));
				}else{
					$(".noSpecialization").attr('checked', true);
					enableDisableCheckBox($(".noSpecialization"));
				}
			</c:forEach>
		</c:forEach>
		$(groupCSS + ", " + otherCSS).click(function() {
			var changedNode = $(this);
			enableDisableCheckBox(changedNode);
		});
		
		if($("#disableCheckBoxex").val()=='true'){
			var boxList = $('input:checkbox');
			boxList.each(function(i){
				$(this).attr("disabled","disabled");//=true;
            });
		}
	});
	//This method is called while saving specializtion form and check if there are any unsaved changes.
	function saveForm(nextaction) {
		if(nextaction == 'back') {
			var $self=$(this);
			var $form = $("#disableCheckBoxex").closest('form');
			var isSame = false;
			data = $form.serializeArray();
			if(lastData != null){
				if($(lastData).compare($(data))){
					isSame = true;
				}
			}
			if(!isSame && lastData != null){
				$('<div id="dialogBox"></div>').appendTo('body')
				.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
				.dialog({
					modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
					width: 'auto', modal: true, resizable: false, draggable:false,
					dialogClass: 'dialogButtons',
					buttons: {
						OK: function () {
							document.specialization.action = document.specialization.action
							+ '&next_action=' + nextaction 
							+ "&section=<%=request.getAttribute("section")%>&subsection=<%=request.getAttribute("subsection")%>"
							+ "&business_app_id=<%=request.getAttribute("business_app_id")%>"
							+ "&service_app_id=<%=request.getAttribute("service_app_id")%>"+"&elementId="+'${elementId}';
							document.specialization.submit();
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
			}else{
				document.specialization.action = document.specialization.action
				+ '&next_action=' + nextaction 
				+ "&section=<%=request.getAttribute("section")%>&subsection=<%=request.getAttribute("subsection")%>"
				+ "&business_app_id=<%=request.getAttribute("business_app_id")%>"
				+ "&service_app_id=<%=request.getAttribute("service_app_id")%>"+"&elementId="+'${elementId}';
				document.specialization.submit();
			}
		} else {
			document.specialization.action = document.specialization.action
			+ '&next_action=' + nextaction 
			+ "&section=<%=request.getAttribute("section")%>&subsection=<%=request.getAttribute("subsection")%>"
			+ "&business_app_id=<%=request.getAttribute("business_app_id")%>"
			+ "&service_app_id=<%=request.getAttribute("service_app_id")%>"+"&elementId="+'${elementId}';
			document.specialization.submit();
		}
			
	}
	function returnButton(action1){
	
		document.specialization.action=action1;
		document.specialization.submit();
	}
</script>

<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S073_S073R_PAGE, request.getSession()) || CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.TA_S081_PAGE, request.getSession())
		// Start : R5 Added
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())
		// End : R5 Added
		){%>
	<form action="<portlet:actionURL/>" method="post" name="specialization">
    <input type="hidden" name="next_action" value="" />
    <input type="hidden" value="" id="validateForm"/>
	<c:set var="readOnlyValue" value=""></c:set>
	<c:if test="${loReadOnlySection}">
		<c:set var="readOnlyValue" value="disabled='disabled'"></c:set>
	</c:if>
	<input type="hidden" id="disableCheckBoxex" value="${loReadOnlySection}" /> 
	<%
		Boolean lostatus = null;
		if ((request.getAttribute("loReadOnlySection") != null)) {
			lostatus = (Boolean) request.getAttribute("loReadOnlySection");
		}
		String specializationTree = null;
		if (null != request.getAttribute("specializationTree")) {
			specializationTree = (String) request
					.getAttribute("specializationTree");
		}
	%>

	<c:set var="hideServiceSummaryLink" value='<%=request.getParameter("removeNavigator")%>'></c:set>
	<c:if test="${hideServiceSummaryLink eq null || empty hideServiceSummaryLink}">
		<h2><label class='floatLft wordWrap'>Services: ${serviceName}</label>
			<c:choose>
				<c:when test="${loReadOnlySection}">
					<a id="returnSummaryPage" title="Service Summary"  class="floatRht returnButton"  href="<portlet:renderURL><portlet:param name='business_app_id' value='${business_app_id}'/><portlet:param name='elementId' value='${elementId}' /><portlet:param name='section' value='${section}'/><portlet:param name='service_app_id' value='${service_app_id}'/><portlet:param name='subsection' value='summary'/><portlet:param name='next_action' value='checkForService'/><portlet:param name='displayHistory' value='displayHistory'/></portlet:renderURL>">Service Summary</a>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${!loReadOnlySection and (loReadOnlyStatus ne null and (loReadOnlyStatus eq 'Returned for Revision' or loReadOnlyStatus eq 'Deferred'))}">
							<a id="returnSummaryPage" title="Service Summary"  class="floatRht returnButton"  href="<portlet:renderURL><portlet:param name='business_app_id' value='${business_app_id}'/><portlet:param name='elementId' value='${elementId}' /><portlet:param name='section' value='${section}'/><portlet:param name='service_app_id' value='${service_app_id}'/><portlet:param name='subsection' value='summary'/><portlet:param name='next_action' value='checkForService'/><portlet:param name='displayHistory' value='displayHistory'/></portlet:renderURL>">Service Summary</a>
						</c:when>
						<c:otherwise>
							<c:set var="urlApplication"
								value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_add_service&_nfls=false&bussAppStatus=${bussAppStatus}&business_app_id=${business_app_id}&applicationId=${applicationId}&section=servicessummary&subsection=summary&next_action=checkForService">
							</c:set>              
							<a id="returnSummaryPage" title="Service Summary"  class="floatRht returnButton"  href="${urlApplication}">Service Summary</a>
						</c:otherwise>							
					</c:choose>
				</c:otherwise>
			</c:choose>
		</h2>	
			
	</c:if>
	
	<!-- Error display area starts here --> 
	<c:if test="${errorMsg ne null}">
		<div class="failedShow">${errorMsg}</div>
	</c:if>
	<!-- Error display area ends here -->
		
	<p class='clear'></p>
		
	<div class="containerpanel">
	<!-- Form Data Starts -->
		<div id="mymain">
			<h3 class="floatLft">Specialization</h3>
			
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
			<p>For your selected Service listed above, please select Specializations which best describe the unique services offered by your organization.</p> 
		    <p />
		    <div class="roleBasedWrapper">
		    	<%=specializationTree%>
		    </div>
		    <div class="taskreassign" style="padding-right:11px;">
                <p>No specialization within this service applies to my organization
                <input class="noSpecialization" name="nospecialization" ${readOnlyValue} type="checkbox" value="Y" title="Check this box if your organization does not provide any of the specializations listed above."/><p>
	        </div>
		</div>
	</div><!-- Form Data Ends -->
	<br />
		
	<c:if test="${!loReadOnlySection}">	
		<div class="buttonholder">
			<input type="button" class="graybtutton" value="&lt;&lt; Back" title="<< Back" onclick="saveForm('back')"/>
			<input type="button" class="button" value="Save" title="Save" onclick="saveForm('save')" id="saveButtonId"/>
		    <input type="button" class="button" value="Save &amp; Next" title="Save & Next" onclick="saveForm('save_next')"  id="saveAndNextButtonId"/>
		</div>
	</c:if>		
	<!-- Body Container Ends -->
	</form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>