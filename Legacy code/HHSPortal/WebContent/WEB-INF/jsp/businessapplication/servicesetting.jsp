<!-- This page is displayed when a user click on save and next button on  service Specialization screen.
It will display list  services organizations deliver.-->
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
    <style type="text/css">
    .commentHidden{
		display:none;
	}
    	.iconQuestion{
    		margin-left: 6px !important;
    	}
    	.errorMessages{
    		width: 97% !important;
    	}
    	h2{
    		width: 96% !important;
    	}
    </style>
    
<script type="text/javascript">
//Groups multiple checkbox/Other
	groupClass = "selectedSetting";
	otherClass = "noSelectSetting";
	groupCSS = "." + groupClass;
	otherCSS = "." + otherClass;
	$(document).ready(function() {
		<c:forEach var="test" items="${loTaxonomyIdMap}"  varStatus="status">
			<c:forEach var="map1" items="${test}"  varStatus="status">
				var id = "${map1.value}";
				if(id!=null && id!="" && id!=true && id!='Y'){
					$("#input" + id).attr('checked', true);
					enableDisableCheckBox($("#input" + id));
				}else{
					$(".noSelectSetting").attr('checked', true);
					enableDisableCheckBox($(".noSelectSetting"));
				}
			</c:forEach>
		</c:forEach>
		$(groupCSS + ", " + otherCSS).click(function() {
			var changedNode = $(this);
			enableDisableCheckBox(changedNode);
		});
	});
	function saveForm(nextaction){
		document.servicesettingform.action = document.servicesettingform.action+'&next_action='+nextaction
					+ "&section=<%=request.getAttribute("section")%>&subsection=<%=request.getAttribute("subsection")%>"
					+ "&business_app_id=<%=request.getAttribute("business_app_id")%>"
					+ "&service_app_id=<%=request.getAttribute("service_app_id")%>"+"&elementId="+'${elementId}';
		document.servicesettingform.submit();
	}
</script>
<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S074_S074R_PAGE, request.getSession()) || CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.TA_S081_PAGE, request.getSession())
		// Start : R5 Added
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())
		// End : R5 Added
		){%>
<form action="<portlet:actionURL/>" method="post" name="servicesettingform">
	<c:set var="readOnlyValue" value=""></c:set>
	<c:if test="${loReadOnlySection}">
		<c:set var="readOnlyValue" value="disabled='disabled'"></c:set>
	</c:if>

	<input type="hidden" name="next_action" value="" />
    <input type="hidden" value="" id="validateForm"/>
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
									value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_add_service&_nfls=false&bussAppStatus=${bussAppStatus}&business_app_id=${business_app_id}&applicationId=${applicationId}&service_app_id=${service_app_id}&section=servicessummary&subsection=summary&next_action=checkForService"></c:set>              
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
	    	<div class="hr"></div>
	    	<h3 class="floatLft">Service Setting</h3>
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
	    	<p title="Service Setting refers to the type of physical location in which services are provided, specified by nature of a particular population group or specific program design.">
	    	From the list below, please select the specialized Service Setting in which your organization provides the selected Service.
	    	</p>
		    <%
			Boolean lostatus=null;
			if((request.getAttribute("loReadOnlySection") != null)){
				lostatus=(Boolean)request.getAttribute("loReadOnlySection");
			}
			%>
	    	<!-- Grid Start -->
			<div class="tabularWrapper clear">
		        <table cellpadding="0" cellspacing="0" class="grid">
		            <tr>
		                <th>Setting</th>
		                <th>Description</th>
		                <th></th>
		            </tr>
		            <% List<TaxonomyTree> loTaxonomyTree = new ArrayList();
		            if(request.getAttribute("TaxonomyElementList") != null){
		            	loTaxonomyTree = (List<TaxonomyTree>) request.getAttribute("TaxonomyElementList");
		            }
		            Iterator loTaxonomyIter = loTaxonomyTree.iterator();
		            while(loTaxonomyIter.hasNext()){
		            	TaxonomyTree loTaxTree = (TaxonomyTree) loTaxonomyIter.next();
		            	%>
		            	<tr>
		                	<td><%=loTaxTree.getMsElementName()%></td>
		                	<td><%=loTaxTree.getMsElementDescription()%></td>
		                	<td><input id="input<%=loTaxTree.getMsElementid()%>" class="selectedSetting" ${readOnlyValue} name="servicesetting" type="checkbox" value="<%=loTaxTree.getMsElementid()%>" /></td>
		           		</tr>
		            <%}%>
		            <tr class="nobdr">
		                <td colspan="2" class="alignRht" >My organization does not provide selected Service in a specialized setting.</td>
		                <td><input class="noSelectSetting" name="noservicesetting" type="checkbox" value="Y" ${readOnlyValue} title="Check this box if your organization provides the selected Service in a provider setting that is not specialized, such as an office."/></td>
		            </tr>
		        </table>
			</div>
	    	<!-- Grid End -->
			<c:if test="${!loReadOnlySection}">	    
			    <div class="buttonholder">
			    	<input type="button" class="graybtutton" value="&lt;&lt; Back" title="<< Back" onclick="saveForm('back')" />
			    	<input type="button" class="button" value="Save" title="Save" onclick="saveForm('save')" id="saveButtonId"/>
				    <input type="button" class="button" value="Save &amp; Complete" title="Save & Complete" onclick="saveForm('save_next')"  id="saveAndNextButtonId"/>
			    </div>
			</c:if>	    
</form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>