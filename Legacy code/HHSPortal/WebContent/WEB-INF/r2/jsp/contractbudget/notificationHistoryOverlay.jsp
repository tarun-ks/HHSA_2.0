<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nyc.hhs.constants.HHSConstants"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page import="com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ taglib prefix="fmtMessages" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp"%>

<style>

.tabularWrapper table{
			border:0px;
			}
					.tabularWrapper table td{
						padding:1px 1px					
					}
</style>			

<!-- This Jsp is for overlay that displays notification history -->
<div class="alert-box alert-box-GetNotificationHistory">
<div class="content">
	<div id="newTabs">
		<div class="tabularCustomHead">Returned Payments: Notification
			History</div>
		<div class='overlayWrapper formcontainer'
			style="padding-right: 12px; margin-left: 6px;height:290px">
			<div class="messagedivover" id="messagedivover"></div>
			<p><p><b class="boldclass addborder" style="padding-left: 5px;">
				Returned Payments: Notification History</b>
			<br><p>
			<div>
				<div class="tabularWrapper">
					<table id="notificationDataTable" style="width: 540px;">
						<tr>
							<th><b>Date Sent</b></th>
							<th><b>Sent By</b></th>
							<th><b>Sent To</b></th>
						</tr>
						<c:forEach items="${ListNotificationHistory}" var='listItem'
							varStatus="listStatus">
							<c:set var="trClass">oddRows</c:set>
							<c:if test="${listStatus.index % 2 eq 0}">
								<c:set var="trClass">evenRows</c:set>
							</c:if>
							<tr class="${trClass}">
								<td>${listItem.sentDate}</td>
								<td>${listItem.sentBy}</td>
								<td>${listItem.role}</td>
							</tr>
						</c:forEach>
					</table>
				</div>

			</div>


		</div>
		<div class="buttonholder"
			style="padding-right: 12px; margin-top: 5px;">
			<input class="button graybtutton" type="button"
				id="linkedCancelButton" name="cancelButton" title="Close"
				value="  Close  " />

		</div>
	</div>
</div>
<a href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
</div>