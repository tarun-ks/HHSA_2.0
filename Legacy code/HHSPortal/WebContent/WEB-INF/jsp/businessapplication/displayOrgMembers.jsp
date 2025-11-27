<!-- This page is displayed when a user click on  members and user table in the organization profile tab.
It will display a table of members.-->
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<style type="text/css">
</style>
<script type="text/javascript" src="../resources/js/comman.js"></script>
<!-- Start : R5 Condition Added -->
<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.OI_S018_PAGE, request.getSession())
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())){%>
<!-- End : R5 Condition Added -->
<form name="myform" action='<portlet:renderURL>
	<portlet:param name="action" value="manageMembers" />
	<portlet:param name="next_action" value="saveOrgMember" />
	<portlet:param name="app_menu_name" value="header_organization_information" />
	<portlet:param name="subsection" value="memberandusers"/></portlet:renderURL>' method ="post" >



<!-- Body Container Starts -->
<h2>Members &amp; Users </h2>
<div class="hr"></div> 

<p>Manage information about your organization's staff member and account users.</p>
<%--Start Update in R5 --%>
<c:if test="${org_type ne 'agency_org' and ReadOnlyUser ne true }">
    
    <!-- Begin QC8914 R7.2.0 Oversight Role Hide -->
    <% 
 		if(! CommonUtil.hideForOversightRole(request.getSession()))
	{%>
		<div class="buttonholder">
			<input id='saveform' type="submit" value="+ Add Member" title="+ Add Member" name="saveform"/>
		</div>
	<%} %>
	<!-- End QC8914 R7.2.0 Oversight Role Hide -->
	
</c:if>
<%--End Update in R5 --%>
<c:set var="IsAdminRole" value="<%=CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.OI_S018_SECTION, request.getSession())%>"></c:set>
<c:choose>
	<c:when test="${loOrgMemberList ne null and fn:length(loOrgMemberList) gt 0}">
		<div class="tabularWrapper">
             <table cellspacing="0" cellpadding="0"  class="grid" id="completeListId">
             	<tr>
                	<th>Name</th>
                    <th>Office Title</th>
                    <th>Email Address</th>
                    <th>System User</th>
                    <th>Member Status</th>
                    <%--Start Update in R5 --%>
                    <!-- QC8914 R7.2.0 Oversight Role - added last And clause -->
					<c:if test="${org_type ne 'agency_org' and ReadOnlyUser ne true }">
					
					<!-- Begin QC8914 R7.2.0 Oversight Role Hide -->
				    <% 
				 		if(! CommonUtil.hideForOversightRole(request.getSession()))
					{%>
                    	<th>Actions</th>
                    <%} %>
                    <!-- End QC8914 R7.2.0 Oversight Role Hide -->
                    	
                    </c:if>
                    <%--End Update in R5 --%>
				</tr>
				<c:forEach var="completeMemberList" items="${loOrgMemberList}">
                	<c:choose>
                  			<c:when test="${IsAdminRole and completeMemberList.msStaffActiveFlag ne 'Inactive'}">
                  				 <tr>
				                    <td class='wordWrap'><c:if test="${completeMemberList.msAdminPermission eq 'Yes'}">*</c:if>${completeMemberList.msStaffFirstName} ${completeMemberList.msStaffMidInitial} ${completeMemberList.msStaffLastName}</td>
				                    <td>${memberTitle[completeMemberList.msStaffTitle]}</td>
				                    <td class='breakAll'>${completeMemberList.msStaffEmail}</td>
				                    <td>${completeMemberList.msUserStatus}</td>
				                    <td>${completeMemberList.msMemberStatus}</td>
				                    <%--Start Update in R5 --%>
				                    <c:if test="${org_type ne 'agency_org' and ReadOnlyUser ne true}">
				                    
				                    <!-- Begin QC8914 R7.2.0 Oversight Role Hide -->
								    <% 
								 		if(! CommonUtil.hideForOversightRole(request.getSession()))
									{%>
					                    <c:choose>
					                    	<c:when test="${completeMemberList.msUserStatus eq 'Pending' }">
			                    				<td>
			                    					<c:choose>
			                    						<c:when test="">
			                    						</c:when>
			                    						<c:otherwise>
			                    							<a href="<portlet:renderURL>
										                   		<portlet:param name="action" value="manageMembers" />
										                   		<portlet:param name='next_action' value="editUserRequest"/>
										                   		<portlet:param name='app_menu_name' value="header_organization_information"/>
										                   		<portlet:param name='subsection' value="memberandusers"/>
										                   		<portlet:param name='editOrgMemberId' value='${completeMemberList.msStaffId}'/>
																</portlet:renderURL>" title="Review Request">Review Request
															</a>
			                    						</c:otherwise>
			                    					</c:choose>
											    </td>
			                    			</c:when>
					                    	<c:otherwise>
			                    				<td>
								                   	<a href="<portlet:renderURL>
								                   		<portlet:param name="action" value="manageMembers" />
								                   		<portlet:param name='next_action' value="editOrgMember"/>
								                   		<portlet:param name="app_menu_name" value="header_organization_information" />
								                   		<portlet:param name='subsection' value="memberandusers"/>
								                   		<portlet:param name='editOrgMemberId' value='${completeMemberList.msStaffId}'/>
														</portlet:renderURL>" title="Edit Profile">Edit Profile
													</a>
											    </td>
					                    	</c:otherwise>
					                    </c:choose>
					               <%} %>
					               <!-- End QC8914 R7.2.0 Oversight Role Hide -->
					                    
					                </c:if>
					              	<%--End Update in R5 --%>
				                 </tr>
                  			</c:when>
                  			<c:otherwise>
                  				<c:if test="${completeMemberList.msUserStatus ne 'Pending' and completeMemberList.msStaffActiveFlag ne 'Inactive'}">
	                  			 	<tr><%--R5 : added asterik --%>
					                    <td class='wordWrap'><c:if test="${completeMemberList.msAdminPermission eq 'Yes'}">*</c:if>${completeMemberList.msStaffFirstName} ${completeMemberList.msStaffMidInitial} ${completeMemberList.msStaffLastName}</td>
					                    <td>${memberTitle[completeMemberList.msStaffTitle]}</td>
					                    <td class='breakAll'>${completeMemberList.msStaffEmail}</td>
					                    <td>${completeMemberList.msUserStatus}</td>
					                    <td>${completeMemberList.msMemberStatus}</td>
					                   	<%--Start Update in R5 --%>
										<!-- QC8914 R7.2.0 Oversight Role - added last And clause -->
										<c:if test="${org_type ne 'agency_org' and ReadOnlyUser ne true }">
										
										<!-- Begin QC8914 R7.2.0 Oversight Role Hide -->
									    <% 
									 		if(! CommonUtil.hideForOversightRole(request.getSession()))
										{%>
						                   	<td nowrap>
											 	<a href="<portlet:renderURL> 
						                   		<portlet:param name="action" value="manageMembers" />
						                   		<portlet:param name='next_action' value="editOrgMember"/>
						                   		<portlet:param name='app_menu_name' value="header_organization_information"/>
						                   		<portlet:param name='subsection' value="memberandusers"/>
						                   		<portlet:param name='editOrgMemberId' value='${completeMemberList.msStaffId}'/>
												</portlet:renderURL>" title="Edit Profile">Edit Profile</a>
										    </td>
										<%} %>
										<!-- End QC8914 R7.2.0 Oversight Role Hide -->
										   
										</c:if>
			              				<%--End Update in R5 --%>
				                    </tr>
			                    </c:if>
                  			</c:otherwise>
                	</c:choose>
				</c:forEach>
			</table>
		</div>
	</c:when>
	<c:otherwise>
		<div> No Record found</div>
	</c:otherwise>
</c:choose>
* Indicates the user is an Account Administrator
</form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>
</body>
