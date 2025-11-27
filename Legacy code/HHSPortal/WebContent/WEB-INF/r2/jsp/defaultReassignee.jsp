<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<jsp:useBean id="assigneeList" class="com.nyc.hhs.model.AssigneeList"
	scope="request" />
<form name="reassigneeForm" id="reassigneeForm"
	action="<portlet:actionURL/>" method="post">
	<portlet:resourceURL var="submitDefaultAssignment"
		id="submitDefaultAssignment" escapeXml="false">
	</portlet:resourceURL>
	<input type='hidden' value='${submitDefaultAssignment}'
		id='submitDefaultAssignment' />
			<input type='hidden' value='${org_type}'
		id='citytype' />
	<div>
		<c:set var="sectionViewProgress" value="false" />
		<c:if
			test="${org_type eq 'city_org' or role eq 'ACCO_STAFF' or role eq 'PROGRAM_STAFF' or role eq 'FINANCE_STAFF'}">
			<c:set var="sectionViewProgress" value="true" />
		</c:if>
		<div id="newTabs" class='wizardUploadTabs'>
			<div class="tabularCustomHead">Default Task Assignments</div>
			<div class='padLft defaultTaskPadLeft'>Default Task Assignments</div>
			<hr
				style="border-top: 1px solid #B2B2B2; width: 40%; margin-left: 10px; float: left; clear: both">
			<div class="addpaddingDefault">
				<span>Below are the default task assignments for this
					contract and task type. Any changes here will be applicable only
					for future tasks.</span>
				<d:content isReadOnly="${sectionViewProgress}">
					<table class='defaultassign' style="width: 100%">
						<tr class='rowpos'>
							<th></th>
							<th class='headerTextAlign'>Default Assignee</th>
							<th class='headerTextAlign'>Set On</th>
							<th class='headerTextAlign'>Assigned By</th>
							<th class='headerTextAlign'>Other</th>
						</tr>
						<c:forEach var="bean" items="${reassigneeList}">

							<tr class='rowpos'>
								<td class="boldtextDefaultHeader">Level ${bean.taskLevel}</td>
								<td class="boldtextDefaultHeader userDetailAlign"><select
									class='dropBox' name="select">
										<option value="">Unassigned</option>
										<c:forEach var="innerBean" items="${bean.userDetailBean}">
											<option value="${innerBean.msUserId}"
												<c:if test="${bean.userId eq innerBean.msUserId}"> selected </c:if>>${innerBean.msFirstName}</option>
										</c:forEach>
								</select></td>
								<c:choose>
									<c:when test="${empty bean.userId}">
										<td>N/A</td>
										<td>N/A</td>
									</c:when>
									<c:otherwise>
										<td>${bean.msModifiedDate}</td>
										<td>${bean.addedBy}</td>
									</c:otherwise>
								</c:choose>
								<td><input type="checkbox"
									title="Selecting this box will prevent you from changing the default assignment if you re-assign tasks"
									name="askCheckBox${bean.taskLevel}"
									<c:if test="${bean.askFlag eq 'Y' }">checked</c:if> value="Y"><label 
									title="Selecting this box will prevent you from changing the default assignment if you re-assign tasks">Don't
									ask again for default user</label></td>

							</tr>
						</c:forEach>
					</table>
				</d:content>
				<div class="buttonholder">
					<c:choose>
						<c:when test="${sectionViewProgress}">
							<input type="button" title="Cancel"
								class="graybtutton exit-panel" id="restoreCancel"
								value=" Cancel " style="display:none;" />
							<input type="button" title="Save" class="button"
								id="restoreButton" value=" Save "
								onclick="saveNewDeafultAssignee()" style="display:none;" />
						</c:when>
						<c:otherwise>
							<input type="button" title="Cancel"
								class="graybtutton exit-panel" id="restoreCancel"
								value=" Cancel "  />
							<input type="button" title="Save" class="button"
								id="restoreButton" value=" Save "
								onclick="saveNewDeafultAssignee()" />
						</c:otherwise>
						</c:choose>
				</div>
			</div>
		</div>
	</div>

</form>
<a href="javascript:void(0);" id="exitUpload"
	class="exit-panel upload-exit" title="Exit"></a>
