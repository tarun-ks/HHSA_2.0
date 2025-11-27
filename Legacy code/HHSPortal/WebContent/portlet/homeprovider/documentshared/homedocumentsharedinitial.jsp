<!--This page will display when no providers  have Documents shared with Organization on the Home page -->
<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<!-- Body Wrapper Start -->
<form id="myform" action="<portlet:actionURL/>" method="post">
	<%
		if(session.getAttribute("message") == null){
	%>
		<div class="tabularWrapper portlet1Col">
		<c:choose>
				<c:when test="${org_type eq 'provider_org'}">
					<div class="tabularCustomHead">Documents Shared with your Organization</div>	
				</c:when>
				<c:otherwise>
					<div class="tabularCustomHead">Documents Shared with your Agency</div>
				</c:otherwise>
			</c:choose> 
			<table cellspacing="0" cellpadding="0" class="grid">
				<tr>
					<td>
						<p>No organizations have shared documents with you at this time. This section will become active once an organization has granted you access to 1 or more documents.</p>
						<c:if test="${org_type eq 'provider_org'}">
							<p>If you'd like to grant Providers or NYC Agencies view-only access to your documents, you can do so from your <a
					href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=enhanced_document_vault_page&_nfls=false&app_menu_name=header_document_vault&removeNavigator=true&headerClick=true">Document Vault</a>.</p>
						</c:if>
					</td>
				</tr>
			</table>
		</div>
	<%}else{%>
		<div class="messagediv" id="messagediv" style="width:50%"></div>
		<script type="text/javascript">
			  $(".messagediv").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			  $(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
			  $(".messagediv").show();
		</script>	
	<%} %>
</form>
