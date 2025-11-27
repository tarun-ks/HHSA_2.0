<!-- This page is displayed when a user click on  the add review request link on Members and Users screen.-->
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<script type="text/javascript" src="../resources/js/organizationProfile.js"></script>
<style type="text/css">
.overlay {
	background-color: #999;
	opacity: 0.5;
	filter: alpha(opacity = 70);
	position: fixed;
	top: 0px;
	left: 0px;
	z-index: 900;
	display: none;
}
.alert-box {
	background: none repeat scroll 0 0 #FFFFFF;
	display: none;
	position: fixed;
	top: 25%;
	width:43% ;
	z-index: 1001;
}
a.exit-panel {
	position: absolute;
	right: -4px;
	top: -4px;
	height: 39px;
	width: 40px;
	z-index: 1002;
	background: url(../framework/skins/hhsa/images/iconClose.png) no-repeat center center;
}

</style>

<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.OI_S111_PAGE, request.getSession())){%>

<form action="<portlet:actionURL><portlet:param name='action' value='manageMembers'/></portlet:actionURL>" name="editRequestForm" method="post">

<input type="hidden" value="${loStaffDetails.msSystemUser}" id="systemUser" name="systemUser"/>
<input type="hidden" value="${loStaffDetails.isAdminUser}" id="checkAdminUser" name="checkAdminUser"/>
<input type="hidden" value="${userRequestId}" id="userRequestId" name="userRequestId"/>
<input type="hidden" value="" id="next_action" name="next_action"/>
<input type="hidden" value="" id="showExistingMember" name="showExistingMember"/>
<input type="hidden" value="${param.editOrgMemberId}" id="editOrgMemberId" name="editOrgMemberId"/>
<input type="hidden" value="${loStaffDetails.msPermissionLevel}" id="permissionLevelId" name="permissionLevelValue"/>
<input type="hidden" value="${loStaffDetails.msPermissionType}" id="permissionTypeId" name="permissionTypeValue"/>
<input type="hidden" value="${loStaffDetails.msAdminPermission}" id="adminUserId" name="adminUserValue"/>
<input type="hidden" value="" id="existingStaffPhoneId" name="existingStaffPhoneId"/>
<input type="hidden" value="" id="existingStaffTitleId" name="existingStaffTitleId"/>
<input type="hidden" value="" id="memberAsUser" name="memberAsUser"/>
<input type="hidden" value="${memberUserTitle}" id="memberUserTitle" name="memberUserTitle"/>
<input type="hidden" value="" id="memberName" name="memberName"/>
<input type="hidden" value="" id="isLinked" name="isLinked"/>
<!-- *************** Developers Code Starts ***************** --> 
<!-- Container Starts -->
    <!-- Form Data Starts -->
        <!-- R5 changes starts-->
<c:choose>
	<c:when test="${loStaffDetails.msPermissionType eq 'R'}">
		<c:set var="permissionRadio0" value="checked='checked'"/>
	</c:when>
	<c:when test="${loStaffDetails.msPermissionLevel eq 'Level 1' }">
		<c:set var="permissionRadio1" value="checked='checked'"/>
	</c:when>
</c:choose>
        <!-- R5 changes ends-->
<c:choose>
	<c:when test="${loStaffDetails.msPermissionLevel eq 'Level 2' }">
		<c:set var="permissionRadio2" value="checked='checked'"/>
	</c:when>
</c:choose>
<c:choose>
	<c:when test="${loStaffDetails.msAdminPermission eq 'Yes' }">
		<c:set var="adminRadioYes" value="checked='checked'"/>
	</c:when>
</c:choose>
<c:choose>
	<c:when test="${loStaffDetails.msAdminPermission eq 'No' }">
		<c:set var="adminRadioNo" value="checked='checked'"/>
	</c:when>
</c:choose>
    <div id="mymain">
        <div class="formcontainer">
            <h2 class='wordWrap'>New User Request: ${loStaffDetails.msStaffFirstName}&nbsp;${loStaffDetails.msStaffMidInitial}&nbsp;${loStaffDetails.msStaffLastName}</h2>
            
            <div class="hr"></div>
            
            <p> A new user has requested access to the HHS-Accelerator system.</p>
                    <!-- R5 changes starts-->
            <p>- To approve this request, please select a user role and permissions, fill out the member information or link to an existing organization member profile and click the "Approve" button.</p>
                    <!-- R5 changes ends-->
            <p>- To deny this request, please scroll to the bottom of the page and click the "Deny" button.</p>
            <p><span class="required">*</span>Indicates required fields</p>
            
            <h3 id="userInfo">NYC.ID User Information</h3>
			<div id="userInfo">
	            <div class="row" id="firstName"> 
	            	<span class="label">First Name</span><span class="formfield">
	                	<input class="input readonly" type="text" maxlength="32" name="staffFirstName" readonly="readonly" value="${loStaffDetails.msStaffFirstName}"/>
	                </span>
	            </div>
	            <div class="row" id="midName">
	            	<span class="label">Middle Initial</span> <span class="formfield">
	                	<input class="input readonly" type="text"  name="staffMidInitial" readonly="readonly" style="width:42px;" maxlength="1" value="${loStaffDetails.msStaffMidInitial}"></input>
	                </span>
	            </div>
	            <div class="row" id="lastName"> 
	            	<span class="label">Last Name</span><span class="formfield">
	                	<input class="input readonly" type="text" name="staffLastName" maxlength="64" readonly="readonly" value="${loStaffDetails.msStaffLastName}"></input>
	                </span>
	           	</div>
	            <div class="row" id="email"> 
	            	<span class="label">NYC.ID/Email Address</span><span class="formfield">
	                	<input class="input readonly" type="text" maxlength="128" name="staffEmail" readonly="readonly" value="${loStaffDetails.msStaffEmail}"></input>
	                </span>
	            </div>
            </div>
            <!-- Horizontal Row starts --> 
            <!-- Horizontal Row ends -->
                    <!-- R5 changes starts-->
            <div id="systemInfo">
            	<h3 id="permissionsHeading">HHS Accelerator System Permissions</h3>
           		<div class="formcontainer" id="permissions">
	                <div class="row" title="Level Two users can also make changes to the organization's accounting period and legal name."> 
		                <span class="label" style="height:608px;">
	                		<span class="required">*</span>
	                		Choose <b>one</b> type of account to assign to this user: 
		                </span>
		                <span class="formfield" style="width:53%">
		                	<b>Read-Only</b><br />
		                	<table>
		                		<tr style="vertical-align: text-top;">
		                			<td width="5%"><input name="systemRole" type="radio" ${permissionRadio0} value="${loStaffDetails.msPermissionLevel}"  onclick="setPermissionLevelAndTypeValue('Level 1','R')" /></td>
		                			<td width="60%"><p>Read-Only<br /> (Read-Only permissions to the entire system) <br /></p></td>
		                			<td width="35%"></td>
		                		</tr>
		                	</table>
		                	<b>Financials</b><br />
		                	<table>
		                		<tr style="vertical-align: text-top;">
		                			<td width="5%"><input name="systemRole" type="radio" ${permissionRadio1} value="${loStaffDetails.msPermissionLevel}"  onclick="setPermissionLevelAndTypeValue('Level 1','F')" /></td>
		                			<td width="60%"><p>Level One<br /> (Basic Permissions in the Financials, Document Vault and Applications tab, Read-Only Permissions to the Procurement tab) <br /></p></td>
		                			<td width="35%"></td>
		                		</tr>
		                		<tr style="vertical-align: text-top;">
		                			<td><input name="systemRole" type="radio" ${permissionRadio2} value="${loStaffDetails.msPermissionLevel}" onclick="setPermissionLevelAndTypeValue('Level 2','F')"/></td>
		                			<td><p>Level Two<br /> (Basic Permissions PLUS Application, Budget Submission/e-signature, Invoice Submission and Document Sharing Permissions, Read-Only Permissions to the Procurement tab)<br /></p></td>
		                			<td></td>
		                		</tr>
		                	</table>
		                    <b>Procurements</b> <br />
		                    <table>
		                		<tr style="vertical-align: text-top;">
		                			<td width="5%"><input name="systemRole" type="radio" ${permissionRadio3} value="${loStaffDetails.msPermissionLevel}"  onclick="setPermissionLevelAndTypeValue('Level 1','P')" /></td>
		                			<td width="60%"><p>Level One<br /> (Basic Permissions in the Procurements, Document Vault and Applications tab, Read-Only Permissions to the Financials tab) <br /></p></td>
		                			<td width="35%"></td>
		                		</tr>
		                		<tr style="vertical-align: text-top;">
		                			<td><input name="systemRole" type="radio" ${permissionRadio4} value="${loStaffDetails.msPermissionLevel}" onclick="setPermissionLevelAndTypeValue('Level 2','P')"/></td>
		                			<td><p>Level Two<br /> (Basic Permissions PLUS Application, Proposal Submission/e-signature and Document Sharing Permissions, Read-Only Permissions to the Financials tab)<br /></p></td>
		                			<td></td>
		                		</tr>
		                	</table>
		                    <b>Financials and Procurements</b> <br />
		                    <table>
		                		<tr style="vertical-align: text-top;">
		                			<td width="5%"><input name="systemRole" type="radio" ${permissionRadio5} value="${loStaffDetails.msPermissionLevel}"  onclick="setPermissionLevelAndTypeValue('Level 1','FP')" /></td>
		                			<td width="60%"><p>Level One<br /> (Basic Permissions in the Financials, Procurements, Document Vault and Applications tab) <br /></p></td>
		                			<td width="35%"></td>
		                		</tr>
		                		<tr style="vertical-align: text-top;">
		                			<td><input name="systemRole" type="radio" ${permissionRadio6} value="${loStaffDetails.msPermissionLevel}" onclick="setPermissionLevelAndTypeValue('Level 2','FP')"/></td>
		                			<td><p>Level Two<br /> (Basic Permissions PLUS Application, Proposal and Budget Submission/e-signature, Invoice Submission and Document Sharing Permissions)</p></td>
		                			<td></td>
		                		</tr>
		                	</table>
						</span>
		            </div>
                	<div class="row"> <span class="label" style='height:54px;'><span class="required">*</span>Would you like to assign this user the same permissions you have as an Account Administrator?:</span><span class="formfield">
	                    <input name="accountAdmin" type="radio" value="${loStaffDetails.msAdminPermission}" ${adminRadioYes} onclick="setAdminUser('Yes')"/>
	                    Yes, this user can also approve account requests and assign user roles.<br />
	                    <input name="accountAdmin" type="radio" value="${loStaffDetails.msAdminPermission}" ${adminRadioNo} onclick="setAdminUser('No')"/>
	                    No </span>
	                </div>
	            </div>
	                    <!-- R5 changes ends-->
                <!-- Horizontal Row starts --> 
                 
                <!-- Horizontal Row ends -->
                
                
			 <!--  h3>Existing Profile ?</h3-->
           	 <div class="buttonholder">
                 <input type="button" class="button" value="Check for Existing Profile" title="Check for Existing Profile" onclick="displayExistingMember(this)"/>
             </div>
             <div class='floatLft individualError' id="linkToMember"></div>    
			</div>
            <p></p>
            <div id="memberInfo" style="display: none">      
                <h3>Member Information</h3>
				<div class="row"> 
             		<span class="label"><span class="required">*</span>Office Title:</span><span class="formfield">
	                    <select class="input" name="staffTitle" ${loStaffDetails.readOnly} id="staffTitle">
	                    	<option value="-1">--- Select one ---</option>
	                    	<c:forEach var="memberTitle" items="${memberTitle}">
	                    		<option value="${memberTitle.key}">${memberTitle.value}</option>
							</c:forEach>
	                    </select>
	                    <c:if test="${errorMsg ne null and !empty errorMsg }">
	                    	<span class="individualError">${errorMsg}</span>
	                    </c:if>
                    </span>
				</div>
                <div class="row"> 
                	<span class="label">Office Phone Number:</span> <span class="formfield">
                    	<input class="input" type="text" value="" ${loStaffDetails.readOnly} maxlength="12" name="staffPhone" id="staffPhone" />
                    </span> 
                </div>
            </div>
		</div>
	</div>
    <div class="formcontainer"></div>
    <!-- Form Data Ends -->
            
    <div class="buttonholder">
        <input type="submit" class="button redbtutton" value="Deny" title="Deny" onclick="userRequest('denyUserRequest')"/>
        <input type="button" class="button" value="Approve" id="Approve" title="Approve" disabled="disabled" onclick="userRequest('approveUserRequest')"/>
    </div>

	<div class="overlay"></div>
	<div class="alert-box">
	    <div id="displayshared" style="display:none">
	    </div>
	    <a href="javascript:void(0);" class="exit-panel"></a>
	 </div>
</form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>
