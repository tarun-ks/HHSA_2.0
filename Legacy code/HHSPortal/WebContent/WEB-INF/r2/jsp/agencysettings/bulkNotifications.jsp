<%--
====================================================================
This JSP is added in R6 as a part of Return Payment Module.
====================================================================
This JSP will render as Bulk Notification Screen for agency user only. 
This JSP will be displayed when Bulk notification tab is selected on 
agency setting screen for agency.
				==================================
All the actions on this page will be handled in agencySetting.js
=====================================================================
 --%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%-- Change in classes for defect 8608 --%>
<style>
.sampleDivBulkNotify{
border-width: 0px;
    position: absolute;
    left: 179px;
    top: 200px;
    width: 500px;
    height: 315px;
    font-family: 'Verdana';
    font-weight: 400;
    font-style: normal;
    font-size: 12px;
    text-align: left;
     background-color: palegoldenrod;
}
.sampleTextNotify{
  border-width:0px;
  position:absolute;
  left:10px;
  top:10px;
  width:490px;
  word-wrap:break-word;
}

.sampleDivInfoNotify{
border-width: 0px;
    position: absolute;
    right: 150px;
    top: 160px;
    width: 500px;
    height: 500px;
    font-family: 'Verdana';
    font-weight: 400;
    font-style: normal;
    font-size: 12px;
    text-align: left;
     background-color: palegoldenrod;
}
.sampleTextInfo{
  border-width:0px;
  position:absolute;
  left:10px;
  top:10px;
  width:490px;
  word-wrap:break-word;
}
</style>	

<portlet:defineObjects />
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/agencySettings.js"></script>

<script type="text/javascript">
	//show selected method.

	showHeaderSelected('header_agencySettings');
</script>
<portlet:actionURL var='sendNotificationAlert' escapeXml='false'>
	<portlet:param name='submit_action' value='sendNotificationAlert' />
	<portlet:param name="status" value="${status}" />
</portlet:actionURL>

<c:set var="AGENCY_S405_PAGE"><%=HHSComponentMappingConstant.AGENCY_S405_PAGE%></c:set>

<portlet:resourceURL var="exportBulkNotificationList"
	id="exportBulkNotificationList" escapeXml="false">
</portlet:resourceURL>
<input type='hidden' value='${exportBulkNotificationList}'
	id='exportBulkNotificationList' />

<%-- resourceURL Added in R6 for Bulk Notifications --%>
<portlet:resourceURL var='getAgencySetAssgndUsrInfo'
	id='getAgencySetAssgndUsrInfo' escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${getAgencySetAssgndUsrInfo}'
	id='hiddenLevelsOfReviewURL' />

<%-- resourceURL and hidden variable for ajax call --%>
<portlet:resourceURL var='saveAgencyLevelUsers'
	id='saveAgencyLevelUsers' escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${saveAgencyLevelUsers}'
	id='hiddenSaveLevelUsers' />

<input type='hidden' value="" id='hiddenAgencySettingUrl' />
<%-- <div id='message' name='message' >'${message}'</div> --%>
<input type='hidden' value='${message}' id='message' name='message' />
<input type="hidden" name="messageType" id="messageType"
	value="${messageType}" />
<div class="customtabs" id="nyc_app_sections">
	<ul class="normalCustomTab" style="padding-left: 0px;">
	<%--QC defect 8608:  class bulkNotificationButton added for agencySetting round corner Tab --%>
		<li id="section_settings" class="bulkNotificationButton" style="margin-right:0px;"><a
			id="header_agencySettings"
			href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceagencysettings&agencysettingTab=agencySettings"
			title="Agency Settings">Agency Settings</a></li>
		<%--QC defect 8608:  class bulkNotificationButton added for agencySetting round corner Tab --%>
		<li id="section_notifications" class="selected bulkNotificationButton" ><a
			id="header_agencySettings"
			href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceagencysettings&agencysettingTab=bulkNotifications"
			title="Bulk Notifications">Bulk Notifications</a></li>
	</ul>
</div>
<input type="hidden" name="sampleNotification" value="${sampleNotification}" id="sampleNotification"></input>
<input type="hidden" name="sampleInformation" value="${sampleInformation}" id="sampleInformation"></input>
<div id="tabs-container">
	<form id="notificationScreen" name="notificationScreen" method="post"
		action="">
		<input type="hidden" id="status" name="status" value="${status}" /> <input
			type="hidden" id="sendNotificationAlert" name="sendNotificationAlert"
			value="${sendNotificationAlert}" />
		<div class="messagediv" id="messagediv"></div>
		<c:if test="${not empty messageType && not empty message}">
			<div class="${messageType}" id="messagediv" style="display: block">
				${message} <img src="../framework/skins/hhsa/images/iconClose.jpg"
					id="box" class="message-close" onclick="showMe('messagediv', this)">
			</div>
		</c:if>
		<div id="transactionStatusDiv" class=""></div>
		<h2>Bulk Notifications</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion">
				<a href="javascript:void(0);" title="Need Help?"
					onclick="viewBulkNotificationInformation();"></a>
			</div>
			<input type="hidden" id="screenName" value="Agency Settings"
				name="screenName" />
		</d:content>
		<div class='hr'></div>
		<p>
			Your Agency may send notifications in bulk to providers with
			unrecouped Advances on a Fiscal Year Budget. First, choose a Fiscal
			Year and Program Name. Then, either select to send notifications to
			providers within this Program or select to export a log of prior
			notifications sent. Note: Notifications are sent only to providers
			with Budgets that meet specific criteria. Please click on the ? icon
			for details on the criteria. <br> <br> 
			<span class="row">
					<a style="text-decoration: underline;" href="#" onclick="viewBulkNotificationSampleNotify()">View Sample Notification</a>
					</span>
					</br>
					</br>
			<label
				class="required">*</label>Indicates a required field.
		</p>
					
		<div class='row'>
			<span class="label"><label class="required">*</label>Fiscal
				Year:</span> <span class="aligntext"> <input type="hidden"
				value="${bulkNotificationBean.fiscalYear}" id="fiscalYear" /> <select
				id="fiscalYearTab" name="fiscalYearTab"
				onchange="enableDisableNotificationBttn(this)">
					<option selected="" value=" ">&nbsp;</option>
					<c:forEach var="listItems" items="${loFiscalYear}">
						<option value="${listItems}" id="${listItems}"
							title="${listItems}">${listItems}</option>
					</c:forEach>

			</select>
			</span>
		</div>
		<br>
		<div class='row'>
			<span class="label"><label class="required">*</label>Program
				Name:</span> <span class="aligntext"> <select id="agencyProgramName"
				class="bulkNotificationAction" name="agencyProgramName"
				onchange="enableDisableNotificationBttn(this)">
					<option selected="" value=" ">&nbsp;</option>
					<c:forEach var="listItems" items="${loProgramNameList}">
						<option value="${listItems.programId}" id="${listItems.programId}"
							title="${listItems.programName}">${listItems.programName}</option>
					</c:forEach>
			</select>
			</span>
		</div>
		<br>
		<div class='row'>
			<span class="label"><label class="required">*</label>Action:</span> <span
				class="aligntext"> <select id="agencyActionTab"
				name="agencyActionTab" class="bulkNotificationAction"
				onchange="enableDisableNotificationBttn(this)">
					<option selected="" value=" ">&nbsp;</option>
					<option value="sendNotification" title="Send Bulk Notifications">Send
						Bulk Notifications</option>
					<option value="exportNotification"
						title="Export Bulk Notifications">Export Bulk
						Notifications</option>
			</select>
			</span>
		</div>
		
		<div id="bulkNotificationDiv" class="sampleDivBulkNotify"
					data-label="Notification flyout detailed"
					style="visibility: hidden; z-index: 1074; display: BLOCK;" >
					<!-- Unnamed () -->
					<div id="NotificationText" class="sampleTextBulkNotify">
					<!--  Fix for QC defect : 8611, to show 'X' on sample notification pop up -->
					<a onclick="closebulkNotificationDiv();" style="cursor: pointer;float: right;margin-right: 10px;" title="Close">[X]</a>
					${sampleNotification}
						</div>
				</div>
				
		<div id="bulkNotificationInformationDiv" class="sampleDivInfoNotify"
					data-label="Notification flyout detailed"
					style="visibility: hidden; z-index: 1074; display: none;" >
					<!-- Unnamed () -->
					<div id="NotificationInformationText" class="sampleTextInfo">
					${sampleInformation}
						</div>
				</div>		
		
		<div class='row'>
			<input type="button" id="bulkNotificationSubmit" class="button"
				style="margin-top: 30px; margin-left: 16%;" name="Submit" disabled
				onclick="launchSubmitNotificationForm()" value="Submit">
		</div>

		<div>&nbsp;</div>
		<div id='agencyContainer' style="display: none"></div>

	</form>
</div>
<div class="overlay"></div>
<div class="alert-box-help">
	<div class="tabularCustomHead toplevelheaderHelp"></div>
	<div id="helpPageDiv"></div>
	<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box-contact">
	<div id="contactDiv"></div>
</div>

<div class="alert-box alert-box-delete alert_bulk_notification">
	<div class="content">
		<div id="newTabs" class='wizardTabs'>
			<%-- updated for defect 8600 --%>
			<div class="tabularCustomHead">
				Confirm Bulk Notifications <a href="javascript:void(0);"
					class="exit-panel nodelete" title="Exit"></a>
			</div>
			<div id="deleteDiv" class="linePadding">
				<b class="boldclass"><br>Confirm Bulk Notifications</b>
				<%-- updated for defect 8600 --%>
				<hr class="restoreHeader" align="left" />
				<div class="pad6 clear promptActionMsg">
					Are you sure you want to send notifications to providers with
					unrecouped Advances on a Fiscal Year Budget? <br> <br>
					The notification will require the providers to log into the system
					for specific details regarding their budget. Specific details will
					not be listed in the e-mail.
				</div>
				<div class="buttonholder txtCenter pad6">
					<input type="button" title="Cancel" class="graybtutton exit-panel"
						id="cancelBulk" value="   Cancel   " /> <input type="button"
						title="Yes, Send Notifications" onclick="submitNotificationForm()"
						class="greenbtutton" id="submitBulk"
						value="Yes, Send Notifications" />
				</div>
			</div>
		</div>
	</div>
	<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit">&nbsp;</a>
</div>