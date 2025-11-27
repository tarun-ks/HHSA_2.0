<!-- This page is displayed when a user click on  the add member button on Members and Users screen.
Here we can add a new Organization Member -->
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>
<html>
	<head>
		<title>NYC_Human Health Services Accelerator</title>
		<script type="text/javascript" src="../resources/js/organizationProfile.js"></script>
	</head>
	<body>
	<!-- Body Wrapper Start -->
	<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.OI_S019_PAGE, request.getSession())){%>
		<form  action="<portlet:actionURL/>" method ="post" name="orgForm">
		<input type="hidden" id="selectStaffValue" name="selectStaffValue" value="<%=request.getAttribute("selectedStaff")%>"></input>
		    <div class="editStaffId" id="editStaffId"> 
		        <div id="mymain">
		            <div class="formcontainer">
		                <h2>Add Organization Member</h2>
		                	<span class="hr"></span>
		                	<p>Fill in the following information for the organization member. </p>
		                	<p>
		                		<span class="required">*</span>Indicates required fields
		                	</p>
							<h3>Member Information</h3>
		            		<div id="memberNotUser">
		                	<div class="row"> <span class="label"><span class="required">*</span>First Name:</span>
			                	<span class="formfield">
			                    	<input  class="input" type="text" id ="staffFirstName" name="staffFirstName" maxlength="32" value="${loStaffDetails.msStaffFirstName}" />
			                    </span>
		                    </div>
		                    
		                	<div class="row"> <span class="label">Middle Initial</span>
			                	<span class="formfield">
			                    	<input class="input" type="text" id ="staffMidInitial" name="staffMidInitial" id="staffMidInitial" value="${loStaffDetails.msStaffMidInitial}" style="width:25px;" maxlength="1" />
			                    </span>
		                    </div>
		                
		                	<div class="row"> <span class="label"><span class="required">*</span>Last Name:</span>
		                		<span class="formfield">
		                    		<input  class="input" type="text" id ="staffLastName"  name="staffLastName" maxlength="64" value="${loStaffDetails.msStaffLastName}"/>
		                    	</span>
		                    </div>
		                	<div class="row"> <span class="label"><span class="required">*</span>Office Title:</span>
			                	<span class="formfield">
			                    <select class="input" name="staffTitle" id="staffTitle">
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
		                	<div class="row"> <span class="label">Office Phone Number:</span>
		                		<span class="formfield">
		                    		<input class="input" id="staffPhone" type="text" name="staffPhone" maxlength="12" value="${loStaffDetails.msStaffPhone}"/>
		                    	</span>
		                    </div>
		               	 	<div class="row"> <span class="label"><span class="required">*</span>Email Address:</span>
		               	 		<span class="formfield">
		                    		<input class="input" type="text" name="staffEmail" maxlength="128" id="staffEmail" value="${loStaffDetails.msStaffEmail}"/>
		                    	</span>
		                    </div>	               
			                <div class="buttonholder">
			                	<input id="cancelform" type="button" class="graybtutton" value="Cancel" title="Cancel" name="cancel" onclick="cancelButton('cancleAction')"/>
			                	<input id='saveform' type="button" value="Save" name="saveform" title="Save" onclick="submitForm('addUser','<%=renderRequest.getAttribute("section")%>');"/>
			            	</div>
		        		</div>
		    		</div>
				</div>
			</div>
	</form>
	<% } else {%>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
	</body>
</html>
