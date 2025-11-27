<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.frameworks.grid.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.util.HHSUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<portlet:defineObjects/>


<!--[Start] Add for save update user profile  -->
<script>
var userProfileWindow;

$(document).ready(// for competition pool typehead
		function() {
	 		$("#newProfileWin").click(function() {
	 			userProfileWindow = window.open("<%=HHSUtil.obtainNYCIDurl() %>");
			}); 
	});


</script>

<!-- Body Wrapper Start -->
<form id="myform" action="<portlet:actionURL/>" method ="post" >
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_1, request.getSession())) {%>
		<div  class="tabularWrapper portlet1Col">
			<div class="tabularCustomHead">NYC.ID Account Management</div>
		    <table cellspacing="0" cellpadding="0"  class="grid">  
		    <%--Start : Added in R5 --%>  
		    <%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_SECTION, request.getSession())) {%>
					<tr class="alternate">
		    			<td><span class="portletTextBold">
		    			<%if(Integer.valueOf(request.getAttribute("userAccountCount").toString()) != 0){ %>
		                	<a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=hhsweb_page_manage_users&action=manageMembers&_nfls=false&app_menu_name=header_organization_information&section=basics&subsection=memberandusers&next_action=displayOrgMember&fb_formName=OrgProfile" title='${userAccountCount} User Accounts.'>${userAccountCount}</a>
		            	<%}else{ %> ${userAccountCount}<%} %>  
		    			</span> User account requests requiring action</td>
					</tr>
				<%} %>  
				<%--End : Added in R5 --%> 

 				<tr>
			    	<td>
			    		<a title="Update Your NYC.ID Profile." href="#" id="newProfileWin" name="newProfileWin">Update Your NYC.ID Profile</a>
					</td>
				</tr>
				<tr>
					<td>
						NYC.ID takes several minutes to synchronize with HHS Accelerator.  The next time you log in, your NYC.ID profile will be updated.
					</td>
				</tr>
			</table>
		</div>
	<%}else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</form>	


