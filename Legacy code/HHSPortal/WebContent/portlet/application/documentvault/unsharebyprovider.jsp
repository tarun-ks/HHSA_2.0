<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>
<script type="text/javascript" src="../js/unsharebyprovider.js"></script>
<div class="overlaycontent">

	<form id="unshareformbyprovider" action="<portlet:actionURL/>" method ="post" name="unshareformbyprovider">
		<input type="hidden" id="message" name ="message">
		<input type="hidden" id="messageType" name ="messageType">
		<input type="hidden" id="nextUnshareByProviderAction" name ="next_action">
		<!--Start of R4 Document Vault changes: Component Mapping check added for Agency security matrix-->
		<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S108_PAGE, request.getSession())|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())) {%>
		<!--End of R4 Document Vault changes -->
			<div class="wizardTabs">
				<p>Select an Organization:</p>
				<select id = "providerName" name="providerName" class="input">
				<option value=" "/>
					<c:forEach var="providerBean" items="${providerSet}">
						<option value="<c:out value="${providerBean.hiddenValue}"/>" title="${providerBean.displayValue}">${providerBean.displayValue}</option>
					</c:forEach>
				</select>
				<div id="tableDiv" style="display:none">
					<p>Access to the following documents will be removed for the selected Organization.</p>
					<div  class="tabularWrapper" style='height: 305px !important; overflow: auto;'>
						<table border="1" id='removetable'>
							<tr><th class="heading">Document Name</th>
							<th class="heading"	>Document Type</th></tr>	
						</table>
					</div>
				</div>
				<div>&nbsp;</div>
				<div class="buttonholder">
					<input class="button graybtutton" title="Cancel" type="button" id="cancelunsharebyprovider" value="Cancel"/>
					<input class="button redbtutton" title="- Remove" type="button" value="- Remove" id="removeaccessbyprovider"/>
				</div>
			 </div>
		<%}else{ %>
			<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		<%} %>
	</form>
</div>