<%-- This jsp contains an overlay for notifying provider --%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%-- Commenting below code as part of defect : 8602 fix --%>
<%-- <script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/agencySettings.js"></script> --%>
	
<style>
.sampleDivNotify{
border-width: 0px;
    position: absolute;
    left: 179px;
    top: -100px;
    width: 500px;
    height: 550px;
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
</style>	
<portlet:defineObjects/>
<portlet:resourceURL var='sendNotificationAlert' id='sendNotificationAlert' escapeXml='false'>
</portlet:resourceURL>

<fmt:setBundle basename="com/nyc/hhs/properties/errormessages" />
<%-- Commenting below code as part of defect : 8627 fix --%>
<%-- <script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/contractBudget.js"></script> --%>
<script type="text/javascript"
	src="../resources/js/autoNumeric-1.7.5.js"></script>
	
<%-- Commenting below code as part of defect : 8607 fix --%>
<%-- <script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/contractlist.js"></script> --%>
<%-- This div contains the required tags of notify provider overlay --%>
<div class="alert-box alert-box-Notify-Provider">
	<form id="NotifyProviderForm" name="NotifyProviderForm">${NotificationProviderList}
	<input type="hidden" id="sendNotificationAlert" name="sendNotificationAlert" value="${sendNotificationAlert}" />
	<input type="hidden" id="agencyAction" value="confirmNotification" name="actionSelected" />
	<input type="hidden" id="programName" value=""/>
	<input type="hidden" id="programId" value=""/>
		<div class="content">
			<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">
					Confirm Notification <a href="javascript:void(0);"
						class="exit-panel nodelete" title="Exit"></a>
				</div>
				<h2 class='padLft' style="padding-left: 10px; font-size: 20px;padding-bottom: inherit;">Confirm
					Notification</h2>${NotificationProviderList}
					<%--Css added for Firefox --%>
				<hr class="restoreHeader" style="clear: both;">
				<div id="NotifyDiv" class="linePadding">
					<div align="left">Are you sure that you want to notify the provider of the remaining balance on this budget?</div>
						
					<div>If so, select the provider user group(s) to which the notification should be sent.</div>
					<span class="row">
					<a style="text-decoration: underline;" href="#" onclick="viewNotificationSampleNotify()">View Sample Notification</a>
					</span>
					<br>
					<div class="row">
						<span class="label" style="width: 170px; height:0px;"><label
							class="required">*</label>Send To:</span>
					</div>
					<div id="notificationProvider">
					<select name="notProvider" id="notProviderSelection" onchange="disableButton()"
						style="height: 22px; width: 245px;">
						<option value=""></option>
						<c:forEach items="${NotificationProviderList}" var="option">
							<option value="${option.key}">
								<c:out value="${option.value}"></c:out>
							</option>
						</c:forEach>
					</select>
				</div>
				
				<div id="NotificationDiv" class="sampleDivNotify"
					data-label="Notification flyout detailed"
					style="visibility: hidden; z-index: 1074; display: none;" >
					<!-- Unnamed () -->
					<div id="NotificationText" class="sampleTextNotify">
					<!--  Fix for QC defect : 8611, to show 'X' on sample notification pop up -->
					<a onclick="closeNotificationDiv();" style="cursor: pointer;float: right;margin-right: 10px;" title="Close">[X]</a>
						${sampleNotification}
					</div>
				</div>
					<div class="buttonholder txtCenter">
						<input type="button" class="graybtutton exit-panel"
							value="   Cancel   " /> <input type="button" id="sendNotButton"
							value="Send Notification" onclick="sendNotificationForm()"/>
					</div>
				
			</div>
		</div>
		<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit">&nbsp;</a>
	</form>
</div>