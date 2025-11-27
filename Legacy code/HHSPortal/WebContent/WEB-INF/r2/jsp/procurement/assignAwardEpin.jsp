<%-- This jsp used to display pop up for assigning E Pin when Epin is not assigned --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<portlet:defineObjects />
<%-- Code updated for R4 Starts --%>
<script type="text/javascript">
var requiredKey= "<fmt:message key='REQUIRED_FIELDS'/>";
</script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<%-- Code updated for R4 Ends --%>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/assignawardepin.js"></script>
<%-- Overlay Popup Starts --%>
<portlet:actionURL var="assignAwardEpinUrl" escapeXml="false">
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="topLevelFromRequest" value="AwardsandContracts"/>
	<portlet:param name="midLevelFromRequest" value="AwardsandContractsScreen"/>
	<%-- Code updated for R4 Ends --%>
	<portlet:param name="action" value="awardContract" />
	<portlet:param name="submit_action" value="assignAPTAwardEpin" />
	<portlet:param name="procurementId" value="${procurementId}"/>
	<portlet:param name="contractID" value="${contractID}"/>
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}" />
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
<%-- Code updated for R4 Ends --%>
</portlet:actionURL>

<!-- R6 resource url added for validateEpin before assigning award epin start -->
<portlet:resourceURL var="validateAwardEpinUrl" id="validateAwardEpinUrl" escapeXml="false">
</portlet:resourceURL>
<!-- R6 resource url added for validateEpin before assigning award epin end -->

<form:form id="assignAwardEpinForm" name="assignAwardEpinForm"
	action="${assignAwardEpinUrl}" method="post" commandName="AwardBean">
	<input type="hidden" value="" name="awardEpinId" id="awardEpinId"/> 
	<input type="hidden" name="contractTypeId" id="contractTypeId" value ="${contractTypeId}"/>
	<!--R6: code update for release 6 start -->
	<input type="hidden" name="procurementAgencyId" id="procurementAgencyId" value =""/>
	<input type="hidden" name="refAptEpinId" id="refAptEpinId" value =""/>
	<input type="hidden" name="validateAwardEpinUrl" id="validateAwardEpinUrl" value ="${validateAwardEpinUrl}"/>
	<!--R6: code update for release 6 end -->
	<div class='tabularContainer'>
		<!-- R6: Added error message div on top of overlay for displaying error messages on overlay start -->
		<div class="failed" id="messagediv" style="display:none"> <span id="failedMessage"></span>
			<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" onclick="showMe('messagediv', this)"/>
		</div>
		<!-- R6: Added error message div on top of overlay for displaying error messages on overlay end -->
		<h2>Assign APT Award E-PIN</h2>
		<div>&nbsp;</div>
		<div class="formcontainer">
		<div class="row"><span class="label">Provider Name:</span> <span
			class="formfield">${amountProviderDetails.providerName}</span></div>
		<div class="row"><span class="label">Award Amount($):</span> <span
			class="formfield awardAmt">${amountProviderDetails.awardAmount}</span></div>
			<%-- Code updated for R4 Starts --%>
					<c:if test="${(isFinancials eq null or isFinancials eq '' or isFinancials eq 'false') and (isOpenEndedProc ne null and isOpenEndedProc eq '1')}">
			<div class="row">
				<span class="label"><label class="required">*</label>Contract Start Date:</span> 
				<span class="formfield">
					<input id="contractStartDate" name="contractStartDate" type="text" value="${epinBean.contractStart}" class='datepicker' validate="calender" maxlength="10"/>
					<img src="../framework/skins/hhsa/images/calender.png" class="imgclassPlanned" onclick="NewCssCal('contractStartDate',event,'mmddyyyy');return false;">
				</span>
				<span class="error"></span>
			</div>
				<%-- Code updated for R4 Starts --%>
			<div class="row">
				<span class="label"><label class="required">*</label>Contract End Date:</span> 
				<span class="formfield">
					<input id="contractEndDate" name="contractEndDate" type="text" value="${epinBean.contractEnd}" validate="calender" maxlength="10" class='datepicker'/>	
					<img src="../framework/skins/hhsa/images/calender.png" class="imgclassPlanned" onclick="NewCssCal('contractEndDate',event,'mmddyyyy');return false;">
				</span>
				<span class="error"></span>
			</div>
		</c:if>
			
		</div>
		<%-- Code updated for R4 Ends --%>
		<%-- Grid Starts --%>
		<div id="errorMessage" class="individualError floatLft" style="font-size:12px;"></div>
		<div class='clear'></div>
		<div id='assignAPTAwardEPIN'>
		<div class="tabularWrapper">
		<table cellspacing="0" cellpadding="0" class="grid" id="completeListId">
			<tr>
	<%-- Column for all the pop up --%>
				<th></th>
				<th>Award E-PIN</th>
				<th>Award Agency ID</th>
				<th>Awarded Vendor</th>
				<th>Vendor FMS ID</th>
				<th>Award Amount</th>
			</tr>
			<c:choose>
				<c:when test="${awardEPinDetails ne null and fn:length(awardEPinDetails) ne 0}">
					<c:forEach var="awardEPinList" items="${awardEPinDetails}">
						<tr>
						<!-- R6: increased arguments of radioSelectValue method to also send refAptEpinId and agencyId to portlet start-->
							<td><input type="radio" name="selectRadio" id="selectedRadio" value="${awardEPinList.epin}" 
							onclick="radioSelectValue('${awardEPinList.epin}', '${awardEPinList.refAptEpinId}', '${awardEPinList.procurementAgencyId}')"/></td>
						<!-- R6: increased arguments of radioSelectValue method to also send refAptEpinId and agencyId to portlet end-->
							<td>${awardEPinList.epin}</td>
							<td>${awardEPinList.awardAgencyId}</td>
							<td>${awardEPinList.awardedVendorName}</td>
							<td>${awardEPinList.vendorFmsId}</td>
							<td class="awardAmt">${awardEPinList.awardAmount}</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan = 6>
							<div class="messagedivNycMsg" id="messagedivNycMsg">No record found </div>
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
		</table>
		</div>
		</div>
	<%-- div for Assign E-PIN and Cancel button --%>
		<div class="buttonholder">
			<input type="button" align="right" class="graybtutton" id="doNotCancel" value="Cancel" /> 
			<input type="submit" class="" id="assignEPIN" align="right" disabled="disabled" value="Assign E-PIN"/>
		</div>
	</div>
</form:form>