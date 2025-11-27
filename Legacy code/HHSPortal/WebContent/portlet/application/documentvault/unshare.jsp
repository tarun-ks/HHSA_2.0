<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<portlet:defineObjects/>
<script type="text/javascript" src="../js/unshareall.js"></script>
<div class="overlaycontent">
	<form id="unshareform" name="unshareform" action="<portlet:actionURL/>" method ="post" >
		<input type="hidden" id="message" name ="message">
		<input type="hidden" id="messageType" name ="messageType">
		<input type="hidden" id="nextUnshareAllAction" name ="next_action">
		<!--Start of R4 Document Vault changes: Component Mapping check added for Agency security matrix-->
		<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S109_PAGE, request.getSession()) || CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())) {%>
		<!--End of R4 Document Vault changes -->	
			<div class="wizardTabs">
				<p>Your organization is opting to remove all external access to the following documents. 
				No Providers/NYC Agencies will have access to these files.</p>
				<div  class="tabularWrapper">
				<table>
                       <tr>
                             <th>Document Name</th>
                             <th>Document Type</th>
                       </tr>
                       <c:forEach items="${shareDocumentList}" var="shareDocumentList" varStatus="counter">
                             <tr class=${counter.index % 2 eq 0?'evenRows':'oddRows'}>
                                   <td>${shareDocumentList.docName}</td>
                                   <td>${shareDocumentList.docType}</td>
                             </tr>
                       </c:forEach>
                    </table>
				</div>
				<div class="buttonholder">
					<input class="button graybtutton" type="button" title="Cancel" id="cancelunshare" value="Cancel"/>
					<input class="button redbtutton" type="button" title="Remove" value="Remove" id="removeaccess"/>
				</div>
			</div>
		 <%}else{ %>
		 	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		 <%} %>
	</form>
</div>
