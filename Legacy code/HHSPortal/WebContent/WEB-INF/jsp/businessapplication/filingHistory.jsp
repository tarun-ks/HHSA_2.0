<!-- Added in release 5 for release 5 for module Proposal Activity History-->
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page
	import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<%--Release 5 Proposal Activity and Char 500 History --%>
<script type="text/javascript">
	//This will execute when Previous,Next.. is clicked for pagination
	function paging(pageNumber) {
		document.filingHistoryForm.action = document.filingHistoryForm.action
				+ "&next_action=open&ownerProviderId="
				+ $("#ownerProviderId").val()
				+ "&subsection=filingsManageOrganization&activefilingssFrom="
				+ $("#activefilingssFrom").val() + "&activefilingssTo="
				+ $("#activefilingssTo").val() + "&filingPeriodToMonth="
				+ $("#filingPeriodToMonth").val() + "&filingPeriodFromMonth="
				+ $("#filingPeriodFromMonth").val()
				+ "&fiscalYearFilterToMonth="
				+ $("#fiscalYearFilterToMonth").val()
				+ "&fiscalYearFilterFromMonth="
				+ $("#fiscalYearFilterFromMonth").val() + "&nextPage="
				+ pageNumber;
		document.filingHistoryForm.submit();
	}
	function clearfilingsFilter() {
		$('select').find('option:first').attr('selected', 'selected');
		$("#activefilingssFrom").val('');
		$("#activefilingssTo").val('');
	}
	function checkStartEndDatePlanned(startDate, endDate){
		   if(startDate != '' && endDate != '' && (startDate > endDate))
			 return false;
		   else
			 return true;
	}

	// Check for Date Format (mm-dd-yyyy)
	function isDateFormat(txtDate)
	{
	    var reg = /^(0[1-9]|1[012])([\/-])(0[1-9]|[12][0-9]|3[01])\2(\d{4})$/;
	    return reg.test(txtDate);
	}
	
	function displayFilter() {
		var _DTfrom = true;
		var _DTto = true;
		$('#activefilingssFromError').empty();
		$('#activefilingssToError').empty();
		
		var startDate = new Date($("#activefilingssFrom").val());     
		var endDate = new Date($("#activefilingssTo").val());
		
		if(checkStartEndDatePlanned(startDate,endDate)){
		if (_DTfrom && _DTto) {
            if ($('#activefilingssFrom').val() != ''
				&& isNaN(Date.parse($('#activefilingssFrom').val()))){
				$('#activefilingssFromError').html("! Please enter a valid date");
                _DTfrom = false;}
			
		    if ($('#activefilingssTo').val() != ''
	 			&& isNaN(Date.parse($('#activefilingssTo').val()))){
				$('#activefilingssToError').html("! Please enter a valid date");
                _DTto = false;}
		
		
			if (new Date($('#activefilingssTo').val()) < new Date("1/1/1800")) {
				_DTto = false;
				$('#activefilingssToError')
						.html(
								"! Invalid Date. Please enter a year equal to or after 1800");
			}
			
			if (new Date($('#activefilingssFrom').val()) < new Date("1/1/1800")) {
				_DTfrom = false;
				$('#activefilingssFromError')
						.html(
								"! Invalid Date. Please enter a year equal to or after 1800");
			}
			
			if ($('#activefilingssFrom').val() != ''
					&& !isDateFormat($('#activefilingssFrom').val())) {
				$('#activefilingssFromError').html("! Please enter a valid date");
				_DTfrom = false; 
			}
			if ($('#activefilingssTo').val() != ''
					&& !isDateFormat($('#activefilingssTo').val())) {
				
				$('#activefilingssToError').html("! Please enter a valid date");
				_DTto = false;
			}
		}

		if(_DTfrom && _DTto){
		document.filingHistoryForm.action = document.filingHistoryForm.action
				+ "&next_action=open&ownerProviderId="
				+ $("#ownerProviderId").val()
				+ "&subsection=filingsManageOrganization&activefilingssFrom="
				+ $("#activefilingssFrom").val() + "&activefilingssTo="
				+ $("#activefilingssTo").val() + "&filingPeriodToMonth="
				+ $("#filingPeriodToMonth").val() + "&filingPeriodFromMonth="
				+ $("#filingPeriodFromMonth").val()
				+ "&fiscalYearFilterToMonth="
				+ $("#fiscalYearFilterToMonth").val()
				+ "&fiscalYearFilterFromMonth="
				+ $("#fiscalYearFilterFromMonth").val() + "&nextPage=1";
		document.filingHistoryForm.submit();
		}
		}
		else
		{
			$("#activefilingssToError").html("! End Date can not be less than Start Date.");
		}
	}
	//This method set the visibility of pop up up whether it should be enable or disable.
	function setVisibility(id, visibility) {
		$('#activefilingssFromError').empty();
		$('#activefilingssToError').empty();
		callBackInWindow("closePopUp");
		if ($("#" + id).is(":visible")) {
			document.filingHistoryForm.reset();
		}
		$("#" + id).toggle();
	}
</script>

<portlet:defineObjects />
<portlet:actionURL var="filterFilingHistoryUrl" escapeXml="false">
	<portlet:param name="action" value="OrgInformation" />
</portlet:actionURL>


<h2>Filings</h2>

<div class="formcontainer filingsHistory">
	<div class="row">
		<span class="label">Filings Status:</span> <span class="formfield">${filingDetailsBeanKey.FILING_STATUS}</span>
	</div>
	<c:if
		test="${(filingDetailsBeanKey.CORPORATE_STRUCTURE ne 'For Profit') and (filingDetailsBeanKey.FILING_STATUS ne 'Exempt')}">
		<div class="row">
			<span class="label">Fiscal Period of Last Approved Filing:</span> <span
				class="formfield">${filingDetailsBeanKey.LAST_APPROVED_PERIOD}
			</span>
		</div>
		<div class="row">
			<span class="label">Last CHAR500 approved on:</span> <span
				class="formfield">${filingDetailsBeanKey.LAST_APPROVED_DATE}</span>
		</div>
		
		<div class="row">
			<span class="label">Next CHAR500 due date:</span> <span
				class="formfield">${filingDetailsBeanKey.DUE_DATE_TO_DISPLAY}<c:if test="${!(empty filingDetailsBeanKey.FY)}">(FY${filingDetailsBeanKey.FY})</c:if> </span>
				
		</div>
		<div class="row">
			<span class="label">Registration Type:</span> <span class="formfield">${filingDetailsBeanKey.REGISTRATION_TYPE}</span>
		</div>
		<div class="row">
			<span class="label">Last CHAR500 uploaded on:</span> <span
				class="formfield">${filingDetailsBeanKey.LAST_UPLOADED_DATE}</span>
		</div>

	</c:if>




	&nbsp;&nbsp; </br>
	<%--Filter and Reassign section starts --%>
	<div class="taskButtons nowrap">
		<span class='floatLft'> <input type="button"
			value="Filter Items" class="filterDocument floatLft marginReset"
			onclick="setVisibility('documentValuePop', 'inline');" />
		</span> </br>
		<form id="filingHistoryForm" name="filingHistoryForm"
			action="${filterFilingHistoryUrl}" method="post"
			commandName="ApplicationAuditBean">
			<input type="hidden" value="${ownerProviderId}" id="ownerProviderId">
			&nbsp;&nbsp;



			<%--  Popup for Filter Task Starts  --%>
			<div id="documentValuePop" class='formcontainerFinance'
				style='width: 460px;'>
				<div class='close'>
					<a href="javascript:setVisibility('documentValuePop', 'none');">X</a>
				</div>
				<div class='row'>
					<span class='label'>Date/Time From:</span> <span class='formfield'>
						<span class='floatLft'> <input id="activefilingssFrom" type="text"
							validate="calender" value="${activefilingssFrom}"
							class="datepicker" maxlength="10" /><img
							src="../framework/skins/hhsa/images/calender.png"
							onclick="NewCssCal('activefilingssFrom',event,'mmddyyyy');return false;"/>
							&nbsp; &nbsp;
					</span> <span id="activefilingssFromError" class="error clear"></span> to:

						<span> <input id="activefilingssTo" type="text"
							value="${activefilingssTo}" validate="calender"
							class="datepicker" maxlength="10" /><img
							src="../framework/skins/hhsa/images/calender.png"
							onclick="NewCssCal('activefilingssTo',event,'mmddyyyy');return false;"/>
					</span> <span id="activefilingssToError" class="error clear"></span>
					</span>
				</div>

				<div class='row'>
					<span class='label'>Filing Period:</span> <span class='formfield'>
						<span class='floatLft'> <select id="filingPeriodToMonth"
							class="filingPeriodToMonth">
								<option value=""></option>
								<c:forEach items="${filingsDropDownList}" var="filingsDropDown">
									<option value="${filingsDropDown}"
										<c:if test="${ filingsDropDown == filingPeriodToMonth}"> selected </c:if>>
										${filingsDropDown}</option>
								</c:forEach>
						</select> &nbsp;
					</span>

					</span> <span id="" class="error clear"></span>
				</div>

				<div class="buttonholder">
					<input type="button" class='graybtutton' value="Clear Filters"
						onmousedown="clearfilingsFilter();" onclick="clearfilingsFilter()" />
					<input type="button" value="Filter" onclick="displayFilter()" />
				</div>

			</div>

			<%-- Popup for Filter Task Ends --%>

			&nbsp;&nbsp;

			<div class='tabularWrapper gridfixedHeight filingsListDiv'
				style='min-height: 700px !important; width: 920px;'>

				<st:table objectName="loApplicationAuditBeanList" cssClass="heading"
					alternateCss1="evenRows" alternateCss2="oddRows"
					pageSize="${allowedObjectCount}">
					<st:property headingName="Filing Period"
						columnName="msFilingPeriod" size="10%" />
					<st:property headingName="Task" columnName="msEntityIdentifier"
						size="20%">
						<st:extension
							decoratorClass="com.nyc.hhs.frameworks.grid.FilingsHistoryExtension" />
					</st:property>
					<st:property headingName="Action" columnName="msEventname"
						size="15%" />
					<st:property headingName="Detail" columnName="msData" size="20%" />
					<st:property headingName="User" columnName="msUserid" size="10%" />
					<st:property headingName="Date/Time" columnName="msDate" size="15%" />

				</st:table>
				<c:if test="${fn:length(loApplicationAuditBeanList) eq 0 }">
					<div class="noRecordPaymentDiv noRecord" id="noRecordPaymentDiv">No
						filings history exists for this provider</div>
				</c:if>
			</div>


		</form>
	</div>
</div>

