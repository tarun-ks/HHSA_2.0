<!-- This page is displayed when a user click on  the Service summary tab.
Here we can click on add Services to add new service-->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
    
<script>
	//This method open the service page having various services to select from
	function openServicePage() {
	   	document.formName.action = document.formName.action+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+"&section=servicessummary&subsection=addservice&next_action=showServices";
	   	document.formName.submit();
	}
	$(function(){
		showSelected('${section }','${subsection }');
		<c:forEach var="map1" items="${loBusinessStatusBeanMap}">
			setStatusSection("${map1.key}", "${map1.value.msSectionStatus}");
		</c:forEach>
	});
</script>
<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S063_S064_PAGE, request.getSession())
	// Start : R5 Added
	|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())
	// End : R5 Added	
	){%>
	<form action="<portlet:actionURL/>" method="post" name="formName">
		<h2>Services Summary</h2>
        <div class="overlay"></div>
		<div class="alert-box-contact">
			<div class="content">
				<div id="newTabs">
					<div id="contactDiv"></div>
				</div>
			</div>
		</div>
		<div class="hr"></div>
	              
        <div class="greyBox login_info">
          	<p>You have not added any Services to your HHS Accelerator Application. </p> 
	        <p>	 
	        	 The City of New York issues RFPs for a wide range of Client and Community Services. 
	             Each type of Service has been cataloged with definitions for your convenience.  
				 At least one Service must be added to complete your HHS Accelerator Application.  
				 By adding a Service, you are indicating that your organization is capable of delivering it, and you will be asked to provide supporting information. 
				 You may select multiple Services at this time or add additional Services at a later date.  
			</p>  
	
			<p>
				 Based on a positive review of responses and supporting information, your organization will be eligible to view related RFPs and submit Proposals for contracts. 
			</p> 
			<!-- Start : R5 Added -->
			<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S063_S064_PAGE, request.getSession())){%>
			<p>
	       	<strong>
	       		 Please click the 'Add Services' button below to start the Service selection process.
	       	</strong>
	       </p>
	       
	       <p>
	       	<c:set var="isReadOnlyUser" value=""></c:set>
			<c:if test="${servicesReadOnlyUser eq true }">
				<c:set var="isReadOnlyUser" value="disabled"/>
			</c:if>
	         <div class="buttonholder" style="text-align: center;">
           		<input type="button" class="button" value="+ Add Services" ${isReadOnlyUser} title="+ Add Services" onclick="openServicePage()" />
				<input type="hidden" name="next_action" value="" ${isReadOnlyUser} id="showServices"> 
	         </div>
	       </p>
	       <%} %>
	       <!-- End : R5 Added -->
       </div>


	</form>
	
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>		