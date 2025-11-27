<!-- This page is displayed when a user click on  the add edit profile link on Members and Users screen.
Here we can edit existing Organization Member -->
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<portlet:defineObjects />

<script type="text/javascript" src="../resources/js/organizationProfile.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>

<style>
.ui-dialog-buttonpane{
	border:0 !important;
	margin:0 !important;
	padding:0 1em 0.5em 0 !important;
}
.ui-dialog{
	padding:0 
}
.ui-dialog .ui-dialog-titlebar span{
	font-size:12px
}
</style>

<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.OI_S020_PAGE, request.getSession())){%>
<form action="<portlet:actionURL><portlet:param name="action" value="manageMembers" /></portlet:actionURL>" method="post" name="orgForm">

<input type="hidden" value="${loStaffDetails.msSystemUser}" id="systemUser" name="systemUser"/>
<input type="hidden" value="${loStaffDetails.isAdminUser}" id="checkAdminUser" name="checkAdminUser"/>
<input type="hidden" value="${staffId}" id="editStaffId" name="editStaffId"/>
<input type="hidden" value="${memberUserTitle}" id="memberUserTitle" name="memberUserTitle"/>
<input type="hidden" value="${loStaffDetails.msPermissionLevel}" id="permissionLevelId" name="permissionLevelValue"/>
<input type="hidden" value="${loStaffDetails.msPermissionType}" id="permissionTypeId" name="permissionTypeValue"/>
<input type="hidden" value="${loStaffDetails.msAdminPermission}" id="adminUserId" name="adminUserValue"/>
<input type="hidden" value="${loStaffDetails.msStaffActiveFlag}" id="userFlag" name="userFlag"/>
<input type="hidden" value="${lsAdminCount}" id="lsAdminCount" name="lsAdminCount"/>
<input type="hidden" value="${loStaffDetails.msUserStatus}" id="userStatus" name="userStatus"/>
<input type="hidden" value="${isMemberAsUser}" id="isMemberAsUser" name="isMemberAsUser"/>
<input type="hidden" value="${deactivateUser}" id="deactivateUser" name="deactivateUser"/>
<input type="hidden" value="${loStaffDetails.msAdminPermission}" id="adminUserIdNo" name="adminUserIdNo"/>
<input type="hidden" value="${userEmail}" id="userEmail" name="userEmail"/>
<input type="hidden" value="${loStaffDetails.msStaffEmail}" id="emailIdToBeDeactivated" name="emailIdToBeDeactivated"/>
<%

String userEmail="";
if(renderRequest.getAttribute("userEmail")!=null){
	userEmail = (String)renderRequest.getAttribute("userEmail");
}

%>

<div id="nameDiv">
	<span id="lastNameId" style="display:none">${loStaffDetails.msStaffLastName}</span>
	<span id="firstNameId" style="display:none">${loStaffDetails.msStaffFirstName}</span>
	<span id="middleNameId" style="display:none">${loStaffDetails.msStaffMidInitial}</span>
</div>
<c:choose>
<%-- Start : Changes in R5 --%>
	<c:when test="${loStaffDetails.msPermissionType eq 'R'}">
		<c:set var="permissionRadio0" value="checked='checked'"/>
	</c:when>
	<c:when test="${loStaffDetails.msPermissionLevel eq 'Level 1' and loStaffDetails.msPermissionType eq 'F'}">
		<c:set var="permissionRadio1" value="checked='checked'"/>
	</c:when>
	<c:when test="${loStaffDetails.msPermissionLevel eq 'Level 2' and loStaffDetails.msPermissionType eq 'F'}">
		<c:set var="permissionRadio2" value="checked='checked'"/>
	</c:when>
	<c:when test="${loStaffDetails.msPermissionLevel eq 'Level 1' and loStaffDetails.msPermissionType eq 'P'}">
		<c:set var="permissionRadio3" value="checked='checked'"/>
	</c:when>
	<c:when test="${loStaffDetails.msPermissionLevel eq 'Level 2' and loStaffDetails.msPermissionType eq 'P'}">
		<c:set var="permissionRadio4" value="checked='checked'"/>
	</c:when>
	<c:when test="${loStaffDetails.msPermissionLevel eq 'Level 1' and loStaffDetails.msPermissionType eq 'FP'}">
		<c:set var="permissionRadio5" value="checked='checked'"/>
	</c:when>
	<%-- End : Changes in R5 --%>
	<c:when test="${loStaffDetails.msPermissionLevel eq 'Level 2' and loStaffDetails.msPermissionType eq 'FP'}">
		<c:set var="permissionRadio6" value="checked='checked'"/>
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
<c:set var="IsAdminRole" value="<%=CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.OI_S018_SECTION, request.getSession())%>"></c:set>
    <div id="mymain">
        <div class="formcontainer">
        	<h2 class='wordWrap'>Edit Member Profile: ${loStaffDetails.msStaffFirstName}&nbsp;${loStaffDetails.msStaffMidInitial}&nbsp;${loStaffDetails.msStaffLastName}</h2>
            <%-- Start : Changes in R5 --%>
            
            <%if(null!=session.getAttribute("errorMessage")){%>
		<div id="transactionStatusDivError" class="failed breakAll" style="display:block" >${errorMessage}</div>
	<%
	 session.removeAttribute("errorMessage");}
	%>
          <%-- End : Changes in R5 --%>  
            <div class="hr"></div>
            <p>Edit member information and system permissions.</p>
            <p><span class="required">*</span>Indicates required fields</p>
			
			<c:if test="${isMemberAsUser}">
	            <div id="userInfo">
	            	<h3 id="userInfo">NYC.ID User Information</h3>
		            <div class="row" id="firstName"> 
		            	<span class="label " >First Name</span><span class="formfield">
		                	<input class="input greyBox" type="text" maxlength="32" readonly="readonly" name="staffFirstName"  value="${loStaffDetails.msStaffFirstName}"/>
		                </span>
		            </div>
		            <div class="row" id="midName">
		            	<span class="label">Middle Initial</span> <span class="formfield">
		                	<input class="input readonly greyBox" type="text"  readonly="readonly" name="staffMidInitial" style="width:42px;" maxlength="1" value="${loStaffDetails.msStaffMidInitial}"></input>
		                </span>
		            </div>
		            <div class="row" id="lastName"> 
		            	<span class="label">Last Name</span><span class="formfield">
		                	<input class="input greyBox" type="text" name="staffLastName" readonly="readonly" maxlength="64" value="${loStaffDetails.msStaffLastName}"></input>
		                </span>
		           	</div>
		            <div class="row" id="email"> 
		            	<span class="label">NYC.ID/Email Address</span><span class="formfield">
		                	<input class="input greyBox" type="text" maxlength="128" readonly="readonly" name="staffEmail" value="${loStaffDetails.msStaffEmail}"></input>
		                </span>
		            </div>
	            </div>
			</c:if>
            <% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.OI_S020_SECTION, request.getSession())){%>
            <input type="hidden" value="true" name="adminValue"/>
            <c:if test="${loStaffDetails.msUserStatus ne 'No'}">
	            <div id="systemInfo">
	            	<h3 id="permissionsHeading">HHS Accelerator System Permissions</h3>
	           		<div class="formcontainer" id="permissions">
		                <div class="row" title="Level Two users can also make changes to the organization's accounting period and legal name.">
		                	<span class="label" style="height:608px;">
		                		<span class="required">*</span>
		                		Choose <b>one</b> type of account to assign to this user: 
			                </span>
			                <span class="formfield" style="width:53%">
			                <%-- Start : Changes in R5 --%>
			                	<b>Read-Only</b><br />
			                	<table>
			                		<tr style="vertical-align: text-top;">
			                			<td width="5%"><input name="systemRole" type="radio" ${permissionRadio0} value="${loStaffDetails.msPermissionLevel}"  onclick="setPermissionLevelAndTypeValue('Level 1','R')" /></td>
			                			<td width="60%"><p>Read-Only<br /> (Read-Only permissions to the entire system) <br /></p></td>
			                			<td width="35%"></td>
			                		</tr>
			                	</table>
			                	<%-- End : Changes in R5 --%>
			                	<b>Financials</b><br />
			                	<table>
			                		<tr style="vertical-align: text-top;">
			                			<td width="5%"><input name="systemRole" type="radio" ${permissionRadio1} value="${loStaffDetails.msPermissionLevel}"  onclick="setPermissionLevelAndTypeValue('Level 1','F')" /></td>
			                			<td width="60%"><p>Level One<br /> (Basic Permissions in the Financials, Document Vault and Applications tab, Read-Only Permissions to the Procurement tab) <br /></p></td>
			                			<td width="35%"></td>
			                		</tr>
			                		<tr style="vertical-align: text-top;">
			                			<td><input name="systemRole" type="radio" ${permissionRadio2} value="${loStaffDetails.msPermissionLevel}" onclick="setPermissionLevelAndTypeValue('Level 2','F')"/></td>
			                			<%-- Changes : Changes in R5 --%>
			                			<td><p>Level Two<br /> (Basic Permissions PLUS Application, Budget Submission/e-signature, Invoice Submission and Document Sharing Permissions, Read-Only Permissions to the Procurement tab)<br /></p></td>
			                			<td></td>
			                		</tr>
			                	</table>
			                    <b>Procurements</b> <br />
			                    <table>
			                		<tr style="vertical-align: text-top;">
			                			<td width="5%"><input name="systemRole" type="radio" ${permissionRadio3} value="${loStaffDetails.msPermissionLevel}"  onclick="setPermissionLevelAndTypeValue('Level 1','P')" /></td>
			                			<%-- Changes : Changes in R5 --%>
			                			<td width="60%"><p>Level One<br /> (Basic Permissions in the Procurements, Document Vault and Applications tab, Read-Only Permissions to the Financials tab) <br /></p></td>
			                			<td width="35%"></td>
			                		</tr>
			                		<tr style="vertical-align: text-top;">
			                			<td><input name="systemRole" type="radio" ${permissionRadio4} value="${loStaffDetails.msPermissionLevel}" onclick="setPermissionLevelAndTypeValue('Level 2','P')"/></td>
			                			<%-- Changes : Changes in R5 --%>
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
	                	<div class="row"> <span class="label"><span class="required">*</span>Would you like to assign this user the same permissions you have as an Account Administrator?:</span><span class="formfield">
		                    <input name="accountAdmin" type="radio" value="${loStaffDetails.msAdminPermission}" ${adminRadioYes} onclick="setAdminUser('Yes')"/>
		                    Yes, this user can also approve account requests and assign user roles.<br />
		                    <input name="accountAdmin" type="radio" value="${loStaffDetails.msAdminPermission}" ${adminRadioNo} onclick="setAdminUser('No')"/>
		                    No </span>
		                </div>
		            </div>
	                <!-- Horizontal Row starts --> 
	                 
	                <!-- Horizontal Row ends -->
	                <h3>Deactivate System User?</h3>
	           	 	<div class="formcontainer">
	                <div class="row"> 
	                    <span class="label" style="height:40px;">Indicate if this user should no longer have access to the system:</span>
	                    <span class="formfield">
	                    	<input id="deActivatedUser" name="deActivatedUser" type="checkbox" ${loStaffDetails.msUserStatus eq 'Inactive' ? 'checked' : ''}/>Deactivate User 
	                    </span>
	                  </div>
	             	</div>    
				</div>
			</c:if>
       		<%} %>
            <div id="memberInfo">      
                <h3>Member Information</h3>
                <!-- for members only start -->
             	<c:if test="${!isMemberAsUser}">
	             	<div id="memberNotUser">
		                <div class="row" id="firstName"> 
			            	<span class="label"><span class="required">*</span>First Name:</span><span class="formfield">
			                	<input  id ="staffFirstName" class="input" type="text" maxlength="32" name="staffFirstName" id="staffFirstName" value="${loStaffDetails.msStaffFirstName}"/>
			                </span>
			            </div>
			            <div class="row" id="midName">
			            	<span class="label">Middle Initial:</span> <span class="formfield">
			                	<input id ="staffMidInitial" class="input" type="text" id="staffMidInitial"  name="staffMidInitial" style="width:42px;" maxlength="1" value="${loStaffDetails.msStaffMidInitial}"></input>
			                </span>
			            </div>
			            <div class="row" id="lastName"> 
			            	<span class="label"><span class="required">*</span>Last Name:</span><span class="formfield">
			                	<input id ="staffLastName" class="input" type="text" id="staffLastName" name="staffLastName" maxlength="64" value="${loStaffDetails.msStaffLastName}"></input>
			                </span>
			           	</div>
		            </div>
				</c:if>
	         
	            <!-- for members only end -->
             	<div class="row"> 
             		<span class="label"><span class="required">*</span>Office Title:</span><span class="formfield">
	                    <select class="input" name="staffTitle" id="staffTitle">
		                    <option value="-1">--- Select one ---</option>
	                    	<c:forEach var="memberTitle" items="${memberTitle}">
	                    		<c:choose>
	                    			<c:when test="${loStaffDetails.msStaffTitle eq memberTitle.key}">
	                    				<option selected="selected" value="${memberTitle.key}">${memberTitle.value}</option>
	                    			</c:when>
	                    			<c:otherwise>
	                    				<option value="${memberTitle.key}">${memberTitle.value}</option>
	                    			</c:otherwise>
	                    		</c:choose>
							</c:forEach>
	                    </select>
	                    <c:if test="${errorMsg ne null and !empty errorMsg }">
	                    	<span class="individualError">${errorMsg}</span>
	                    </c:if>
                    </span>
                </div>
                <div class="row"> 
                	<span class="label">Office Phone Number:</span> <span class="formfield">
                    	<input class="input" type="text" id="staffPhone" maxlength="12" value="${loStaffDetails.msStaffPhone}" name="staffPhone" />
                    </span> 
                </div>
                
              <c:if test="${!isMemberAsUser}">
	            <div class="row" id="email"> 
	            	<span class="label"><span class="required">*</span>Email Address:</span><span class="formfield">
	                	<input class="input" type="text" maxlength="128" id="staffEmail" maxlength="60" name="staffEmail" value="${loStaffDetails.msStaffEmail}"></input>
	                </span>
	            </div>
	          </c:if>
	          
			</div>
			<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.OI_S020_SECTION, request.getSession())){%>
	            <h3>Remove from Organization?</h3>
	            <div class="row"> 
	            	<span class="label" title='After removing a member from your organization, you will still be able to view the profile in your "Manage Organization Members" page, but it will be marked as "Inactive".'>Indicate if this person needs to be removed from the organization: <br> You may consider removing a member if they are no longer associated with your organization. If you only want to remove access to the system, please check the Deactivate User box above.</span> 
	            	<span class="formfield">
		            	<c:choose>
		            	 	<c:when test="${loStaffDetails.msMemberStatus eq 'Inactive'}">
		            	 		<input type="checkbox" id="removeMember" name="removeMember" onclick="showHideCalander(this)" checked="checked"/>Remove this member
		            	 	</c:when>
		            	 	<c:otherwise>
		            	 		<input type="checkbox" id="removeMember" name="removeMember" onclick="showHideCalander(this)" />Remove this member
		            	 	</c:otherwise>
		            	</c:choose>
	                </span> 
				</div>
	            <div class="row" id="calanderId">
	            	<c:choose>
	                	<c:when test="${loStaffDetails.msMemberStatus eq 'Inactive'}">
	                	 	<span class="label"><span class="required">*</span>Last Day with Organization: (mm/dd/yyyy):</span>
	                	 	<span class="formfield"> 
	                    		<input onblur="removeError(this)" class="input" type="text" style='width:78px;' id='datepicker' name="datepicker"  validate="calender" maxlength="10"
	                    					value="<fmt:formatDate pattern="MM/dd/yyyy" value="${loStaffDetails.msMemberInactiveDate}" />" />
	                    		<img src="../framework/skins/hhsa/images/calender.png" title="Select a Date" onclick="NewCssCal('datepicker',event,'mmddyyyy');return false;"/>
	                    	 </span>
	                    	 <span class="error" id="datevalidate"></span> 
	                    </c:when>
	                    <c:otherwise>
	                    	<div id="emptyCheckBox" style="display:none">
		                    	<span class="label"><span class="required">*</span>Last Day with Organization: (mm/dd/yyyy):</span>
		                	 	<span class="formfield"> 
		                    		<input onblur="removeError(this)" class="input" type="text" style='width:78px;' id='datepicker' name="datepicker" 
		                    					value="" validate="calender" maxlength="10"/>
		                    		<img src="../framework/skins/hhsa/images/calender.png" title="Select a Date" onclick="NewCssCal('datepicker',event,'mmddyyyy');return false;"/>
		                    	</span>
		                    	<span class="error" id="datevalidate"></span>
	                    	</div>
	                    </c:otherwise>
					</c:choose>
				</div>
       		<%} %>
		</div>
	</div>
    <div class="formcontainer"></div>
    <!-- Form Data Ends -->
           
    <div class="buttonholder">
       <input type="button" class="graybtutton" value="Cancel" title="Cancel" onclick="cancelButton()"/>
       <input type="button" class="button" value="Save" title="Save" onclick="submitForm('editUser','<%=renderRequest.getAttribute("section")%>');"	/>
    </div>
    <!-- Form Data Ends --> 
    <!-- Container Ends --> 

</form>

<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>