<%--
This Jsp is Updated in R6 for return Payment.
Navigation tab is added to navigate between AgencySetting and Bulk Notifications
 --%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<portlet:defineObjects />
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/agencySettings.js"></script>

<script type="text/javascript">
   //show selected method.
   
	showHeaderSelected('header_agencySettings');
</script>
<portlet:actionURL var='getAgencySetAssgndUsrData' escapeXml='false'>
	<portlet:param name='agencySettingContrAction'
		value='getAgencySetAssgndUsrData' />
</portlet:actionURL>

<c:set var="AGENCY_S405_PAGE"><%=HHSComponentMappingConstant.AGENCY_S405_PAGE%></c:set>
<d:content section="${AGENCY_S405_PAGE}"  authorize="">

<%-- resourceURL and hidden variable for ajax call --%>
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
<%--Added in R6 for Return Payment --%>
	<div class="customtabs" id="nyc_app_sections">
	<ul class="normalCustomTab" style="padding-left: 0px;">
	<%--QC defect 8608:  class added for agencySetting round corner Tab --%>
		<li  id="section_settings"  class="selected  bulkNotificationButton" style="margin-right:0px;">
		<a id="header_agencySettings"  href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceagencysettings&agencysettingTab=agencySettings" title="Agency Settings">Agency Settings</a></li>
		<%--QC defect 8608:  class added for agencySetting round corner Tab --%>
		<li id="section_notifications" class="bulkNotificationButton" >
		<a id="header_agencySettings" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenanceagencysettings&agencysettingTab=bulkNotifications" title="Bulk Notifications" >Bulk Notifications</a></li>
	</ul>
 </div>
<div id="tabs-container">
 <%--R6Changes End --%>
<form id="agencySettingAgncyUsr" name="agencySettingAgncyUsr"
	action="<portlet:resourceURL/>" method="post">
	<div id="transactionStatusDiv" class=""></div>
	<h2>Agency Settings</h2>
				   <c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		    <d:content section="${helpIconProvider}">
		     <div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
		          <input type="hidden" id="screenName" value="Agency Settings" name="screenName"/>
		   </d:content> 
	

	<div class='hr'></div>

	<p>There are several review processes your Agency will be expected
		to complete via Tasks. Select a Review Process below to assign users
		to each review level required to complete the corresponding task.</p>


	<div class="formcontainer">
		<div class="row">
			<select id="agencyReviewProcess" name="agencyReviewProcess"
				class='proposalConfigFormfield' onchange="enableDisableGoBttn(this)">
				<c:set var="loagencySettingsBean" value="${agencySettingsBean}"></c:set>
				<option value="rps0" title="Select a Review Process">Select
					a Review Process</option>
				<c:forEach var="listItems"
					items="${loagencySettingsBean.allReviewProcessBeanList}">
					<option value="${listItems.reviewProcessId}" labelAttrib="${listItems.reviewProcessConfigFlag}"
						id="${listItems.reviewProcessDescription}"
						title="${listItems.reviewProcess}">${listItems.reviewProcess}</option>
				</c:forEach>
			</select> <input id="goBttn" name="goBttn" type="button" class="button"
				value="Go" disabled onClick="ajaxCallForUserAssgnd();" />
		</div>
	</div>

	<div>&nbsp;</div>

	<%-- Container Starts --%>

	<div id='agencyContainer' style="display: none"></div>

</form>
</div>
<div class="overlay"></div>
	  <div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>
			<div class="alert-box-contact">
				<div id="contactDiv"></div>
			</div>
</d:content>
