<!-- This page is displayed when a user click on  the add review request link on Members and Users screen.It will contain a link which will open this  page.-->
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<style type="text/css">
<!--
.overlay {
	background-color: #999;
	opacity: 0.5;
	filter: alpha(opacity = 70);
	position: fixed;
	top: 0px;
	left: 0px;
	z-index: 900;
	display: none;
	width: 100%; height: 100%;
}
.alert-box {
	background: none repeat scroll 0 0 #FFFFFF;
	display: none;
	/*height: 268px;*/
	position: fixed;
	/*margin-left: 11%;*/
	/*top: 25%;
	width: 54%;*/
	z-index: 1001;
	vertical-align: middle;
	margin:0 auto;
}
a.exit-panel {
	position: absolute;
	right: -4px;
	top: -4px;
	height: 39px;
	width: 40px;
	z-index: 1002;
	background: url(images/iconClose.jpg) no-repeat center center;
}
-->
</style>

<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.OI_S110_PAGE, request.getSession())){%>
  	<div class=""> <a href="javascript:void(0);"  class="terms" title="Link to Existing Member Profile">Link to Existing Member Profile</a> </div>
	<!-- Overlay Popup Starts -->
	
	<div class="overlaycontent1">

    <div class="tabularCustomHead">Link to Existing Organization Member Profile </div>
	    <div class="tabularWrapper pad6 clear"> 
	    <p>Below is a list of your organization's members that have profile information in HHS Accelerator, but do not have user accounts.
	    Select from the list who the new user should be linked to and click the "Link" button</p>
	    <div id="errorMessage" class="individualError" style="font-size:12px;"></div>
			<c:choose>
				<c:when test="${loOrgMemberList ne null}">
					<div class="tabularWrapper">
		            	<table cellspacing="0" cellpadding="0"  class="grid" id="completeListId">
		                	<tr>
			                  	<th></th>
			                    <th>Name</th>
			                    <th>Office Title</th>
			                    <th>Email Address</th>
		                    </tr>
		                    <c:forEach var="completeMemberList" items="${loOrgMemberList}">
			                    <c:if test="${completeMemberList.msMemberStatus eq 'Active' and completeMemberList.msUserStatus eq 'No'}">
				                  	<tr>
					                  	<td><input type="radio" name="selectRadio" id="selectedRadio" value="${completeMemberList.msStaffId}" 
					                  		onclick="radioSelectValue(this.value,'${completeMemberList.msStaffTitle}','${completeMemberList.msStaffPhone}','${completeMemberList.msStaffFirstName} ${completeMemberList.msStaffMidInitial} ${completeMemberList.msStaffLastName}')"/></td>
					                    <td>${completeMemberList.msStaffFirstName} ${completeMemberList.msStaffMidInitial} ${completeMemberList.msStaffLastName}</td>
					                    <td>${memberTitle[completeMemberList.msStaffTitle]}</td>
					                    <td>${completeMemberList.msStaffEmail}</td>
				                    </tr>
			                    </c:if>
		                  </c:forEach>
		               	</table>
					</div>
					<div class="buttonholder">
		                <input type="button" class="graybtutton" value="Profile not found" title="Profile Not Found" onclick="profileNotFound()"/>
		                <input type="button" class="button" value="Link to Existing Profile" title="Link to Existing Profile" onclick="linkToExistingMember()"/>
		            </div>
				</c:when>
				<c:otherwise>
					<div class="individualError">No record found </div>
				</c:otherwise>
			</c:choose>
	    </div>
    </div>
<!-- Overlay Popup Ends -->
  
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>
   
