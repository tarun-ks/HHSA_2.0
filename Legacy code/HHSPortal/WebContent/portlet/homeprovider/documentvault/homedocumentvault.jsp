<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />

<!-- Body Wrapper Start -->
<form id="myform" action="<portlet:actionURL/>" method="post">
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_6, request.getSession())) {
		if(request.getAttribute("message") == null){
	%>
	<div class="tabularWrapper portlet1Col">
		<div class="tabularCustomHead">Document Vault</div>
		<%int documentCount = (Integer)request.getAttribute("documentCount"); %>
		<table cellspacing="0" cellpadding="0" class="grid">
			<tr>
				<%if(documentCount == 0){
			    %>
					<td><span class="portletTextBold"><%=documentCount%></span>
					Documents in your Document Vault</td>
				<%} else{%>
					<td><span class="portletTextBold"><a
					href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_documentlist&_nfls=false&app_menu_name=header_document_vault&removeNavigator=true" title='<%=documentCount%> Documents.'><%=documentCount%></a></span>
					Documents in your Document Vault</td>
				<%} %>
			</tr>
			<%String dueDate =  (String)request.getAttribute("dueDate");
			if(null != dueDate){
			%>
				<tr class="alternate">
				<td><span class="portletTextBold" style="color: red">!</span>
				Your CHAR500 filing will expire on <%=dueDate%></td>
				</tr>
			<%} %>
		</table>
	</div>
	<%}else{%>
		<div class="messagediv" id="messagediv" style="width:50%"></div>
		<script type="text/javascript">
			  $(".messagediv").html('<%= request.getAttribute("message")%>');
			  $(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
			  $(".messagediv").show();
		</script>	
	<%}}else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</form>
