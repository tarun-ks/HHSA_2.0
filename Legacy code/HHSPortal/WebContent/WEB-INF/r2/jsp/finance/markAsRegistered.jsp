<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%-- This JSP open Mark As Amendment Overlay screen on click of submit button on contract list main screen --%>
<%-- Submit Budget Update Overlay Popup Start --%>
<portlet:defineObjects />

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/markAsRegistered.js"></script>
<%--Define Action mapping on click of Submit Button Start  --%>	
<portlet:resourceURL var="markAsRegistered"
	id="markAsRegistered" escapeXml="false">
</portlet:resourceURL>
<input type="hidden" id="markAsRegisteredUrl"  value="${markAsRegistered}"/>
<%--Define Action mapping on click of Submit Button End  --%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/markAsRegistered.js"></script>
<%--Define resource Mapping to validate User start  --%>
<portlet:resourceURL var="validateUser" id="validateUser" escapeXml="false">
</portlet:resourceURL>
<input type="hidden" name="validateUser" id="validateUser"  value="${validateUser}"/> 
<%--Define resource Mapping to validate User End  --%>

<div class="content">
	<div class='tabularCustomHead'><span id="contractTypeId">Confirm Mark Amendment as Registered
		</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">
	<h2>Mark Amendment as Registered</h2>
	<div class='hr'></div>
	<%-- QC 9433 R 8.1 add dot at the end --%>
		<div class="failed" id="errorMsg"></div>
		<p>Are you sure you want to mark this amendment as Registered? If confirmed, the end date and<br/>
		   value of the contract will be merged with the amendment for the out years.</p>
		<p><label class="required">*</label>Indicates a required field</p>
		
<%-- Form tag Starts --%>
<portlet:actionURL var="viewAmendmentsList" escapeXml="false">
	<portlet:param name="action" value="contractListAction"/>
	<portlet:param name="submit_action" value="amendment"/>
</portlet:actionURL>
<input type="hidden" name="viewAmendmentsList" id="viewAmendmentsList"  value="${viewAmendmentsList}"/>
<form:form id="submitMRForm" action="" method="post" name="submitMRForm">
	<input type="hidden" value="${ContractId}" name="contractId" id="baseContractId"/>
	<input type="hidden" value="${amendcontractid}" name="amendcontractid" id="amendcontractid"/>	
	<input type="hidden" value="${City_ID}" name="cityId" id="cityId"/>
	
<div class="formcontainer">	
	<div class="row">
		<span class='label clearLabel autoWidth'> 
			<input name="" type="checkbox" id='chkSubmitMRForm'/> 
			<label for='chkSubmitMRForm'>I agree to mark amendment as registered.</label>
		</span>
		<span class="error"></span>
	</div>	
	
	<div class="row" id="usernameDiv">
		<span class="label">
			<label class="required">*</label><label for='txtSubmitMRUserName'>User Name:</label>
		</span> 
		<span class="formfield">
			<input type="text" class='proposalConfigDrpdwn' id="txtSubmitMRUserName" name='userName' maxlength="128"/>
		</span> 
		<span class="error"></span>
	</div>
	
	<div class="row" id="passwordDiv">
		<span class="label">
			<label class="required">*</label><label for='txtSubmitMRPassword'>Password:</label>
		</span> 
		<span class="formfield">
			<input type="password" class='proposalConfigDrpdwn' name='password' id="txtSubmitMRPassword" autocomplete="off" />
		</span> 
		<span class="error"></span>
	</div>
</div>
</form:form>

<%-- Form tag End --%>
	<div class="buttonholder">
		<input type="button" class="graybtutton" title="" value="No, Discard" id="btnNotSubmit" /> 
		<input type="button" class="button" title=""	value="Yes, Mark As Registered" id="btnMarkAsRegSubmit" onclick="markAmendmentAsRegistered()" />
	</div>
</div>

<%-- Overlay Popup Ends --%>

</div>