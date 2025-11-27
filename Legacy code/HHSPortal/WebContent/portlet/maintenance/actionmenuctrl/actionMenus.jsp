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
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/actionMenuCtrl.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/taxonomytagging.js"></script>
<%--
<portlet:actionURL var='getAgencySetAssgndUsrData' escapeXml='false'>
	<portlet:param name='agencySettingContrAction'
		value='getAgencySetAssgndUsrData' />
</portlet:actionURL>

 --%>


<%-- resourceURL and hidden variable for ajax call --%>
<portlet:resourceURL var='getAgencyActionMenuInfo' id='getAgencyActionMenuInfo' escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${getAgencyActionMenuInfo}' id='hiddenLevelsOfReviewURL' />


<%-- resourceURL and hidden variable for ajax call --%>
<%-- <portlet:resourceURL var='saveAgencyLevelUsers' id='saveAgencyLevelUsers' escapeXml='false'> --%>
<portlet:resourceURL var='saveAgencyActionMenus' id='saveAgencyActionMenus' escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${saveAgencyActionMenus}' id='hiddenSaveActionMenus' />


 <%--R6Changes End --%>
<form id="agencySettingAgncyUsr" name="agencySettingAgncyUsr" action="<portlet:resourceURL/>" method="post">
	<div id="transactionStatusDiv" class=""></div>
	<h2>Action Menu Settings</h2>
	<div class='linkReturnValut' style='margin-top:8px; margin-right:8px'>
		<a href="javascript:void(0);" onclick="returnToMain();">Return to Maintenance Homepage</a>
	</div>
	
	<div class='hr'></div>

	<div class="formcontainer">
		<div class="row">
			<select id="agencyReviewProcess" name="agencyReviewProcess" class='proposalConfigFormfield' onchange="enableDisableGoBttn(this)">
				<c:set var="loagencySettingsBean" value="${agencySettingsBean}"></c:set>
				<option value="rps0" title="Select a Review Process">Select an Agency</option>
				<c:forEach var="listItems" items="${loagencySettingsBean.allAgencyDetailsBeanList}">
					<option value="${listItems.agencyId}" id="${listItems.agencyId}" title="${listItems.agencyName}">${listItems.agencyName}</option>
				</c:forEach>
			</select> 
			<input id="goBttn" name="goBttn" type="button" class="button" value="Go" disabled onClick="ajaxCallForActionMenus();" />
		</div>
	</div>

	<div>&nbsp;</div>

	<%-- Container Starts --%>

	<div id='agencyContainer' style="display: none"></div>

</form>


<div class="overlay"></div>
	  <div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>
			<div class="alert-box-contact">
				<div id="contactDiv"></div>
			</div>
<script>
</script>
