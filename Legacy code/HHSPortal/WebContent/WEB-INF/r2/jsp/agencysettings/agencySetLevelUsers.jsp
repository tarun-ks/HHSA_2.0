<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<div>
<h4 class='generateListPanel agencyGreyTitle'><label id="levelofRevText" class="bold"></label></h4>

<h3>Description:</h3>
<p id="reviewProcDesc"></p>

<div class='agencySettingWrapper'>
	<h3>Assign Users</h3>
	<p>For each level of review, please assign the users that will have
		the capability to complete the task level.</p>
	<div class='lftContainer'>
		<h3>All Agency Users</h3>
		<c:set var="loAgencySettingsBean" value="${AgencySettingsBean}"></c:set>
		<div class='generateListPanel agencyGreyTitle alignCenter'>All Users</div>
		<select id="allUsers" name="agencyUsers" size="10" onchange="enableDisableAddbtns(this);">
			<c:forEach var="listItems"
				items="${loAgencySettingsBean.allAgencyUsersList}">
				<option value="${listItems.userId}">${listItems.firstName}
					${listItems.lastName} - ${listItems.userRole}</option>
			</c:forEach>
		</select> <input type="hidden" name="hdnAllUsers" value="" id="hdnAllUsers" />
	</div>

	<div class='rhtContainer'>
		<h3>Level 1 Users</h3>
		<div class='generateListPanel agencyGreyTitle alignCenter'>Selected Level 1
			Users</div>
		<select id="level1Users" name="level1Users" multiple="multiple"
			onchange="enableremovebtn(this);">
			<c:forEach var="listItems"
				items="${loAgencySettingsBean.allLevel1UsersList}">
				<c:if test="${listItems.levelId eq 1}">
					<option value="${listItems.userId}">${listItems.firstName}
						${listItems.lastName} - ${listItems.userRole}</option>
				</c:if>
			</c:forEach>
		</select> <input type="hidden" name="hdnLev1Users" value="" id="hdnLev1Users" />
		<div class='taskButtons floatLft'>
			<input type='button' id="level1Add" disabled value='Add User'
				class='add' onclick="addUserToLevel(this);" />
		</div>
		<div class='taskButtons floatRht'>
			<input id="level1Remove" type='button' 
				value='Remove User' class='remove' disabled
				onclick="removeUserFrmLevel(this);enableremovebtn(this);" />
		</div>

		<div id="level2To4Div" name="level2To4Div">
			<h3>Level 2 Users</h3>
			<div class='generateListPanel agencyGreyTitle alignCenter'>Selected Level 2
				Users</div>
			<select id="level2Users" name="level2Users" multiple="multiple"
				onchange="enableremovebtn(this);">
				<c:forEach var="listItems"
					items="${loAgencySettingsBean.allLevel2UsersList}">
					<c:if test="${listItems.levelId eq 2}">
						<option value="${listItems.userId}">${listItems.firstName}
							${listItems.lastName} - ${listItems.userRole}</option>
					</c:if>
				</c:forEach>
			</select> <input type="hidden" name="hdnLev2Users" value="" id="hdnLev2Users" />
			<div class='taskButtons floatLft'>
				<input type='button' id="level2Add" value='Add User' class='add' disabled onclick="addUserToLevel(this);" />
			</div>
			<div class='taskButtons floatRht'>
				<input id="level2Remove" type='button' value='Remove User' class='remove' disabled
					onclick="removeUserFrmLevel(this);enableremovebtn(this);" />
			</div>

			<div id="level3To4Div" name="level3To4Div"
				style="display:none;<c:if test="${loAgencySettingsBean.levelOfReview ge 3}">display:block</c:if>">
				<h3>Level 3 Users</h3>
				<div class='generateListPanel agencyGreyTitle alignCenter'>Selected Level
					3 Users</div>
				<select id="level3Users" name="level3Users" multiple="multiple"
					onchange="enableremovebtn(this);">
					<c:forEach var="listItems"
						items="${loAgencySettingsBean.allLevel3UsersList}">
						<c:if test="${listItems.levelId eq 3}">
							<option value="${listItems.userId}">${listItems.firstName}
								${listItems.lastName} - ${listItems.userRole}</option>
						</c:if>
					</c:forEach>
				</select> <input type="hidden" name="hdnLev3Users" value="" id="hdnLev3Users" />
				<div class='taskButtons floatLft'>
					<input type='button' id="level3Add" value='Add User' class='add' disabled onclick="addUserToLevel(this);" />
				</div>
				<div class='taskButtons floatRht'>
					<input id="level3Remove" type='button' value='Remove User' class='remove' disabled
						onclick="removeUserFrmLevel(this);enableremovebtn(this);" />
				</div>
				<div id="level4Div" name="level4Div"
					style="display:none;<c:if test="${loAgencySettingsBean.levelOfReview eq 4}">display:block</c:if>">
					<h3>Level 4 Users</h3>
					<div class='generateListPanel agencyGreyTitle alignCenter'>Selected Level
						4 Users</div>
					<select id="level4Users" name="level4Users" multiple="multiple"
						onchange="enableremovebtn(this);">
						<c:forEach var="listItems"
							items="${loAgencySettingsBean.allLevel4UsersList}">
							<c:if test="${listItems.levelId eq 4}">
								<option value="${listItems.userId}">${listItems.firstName}
									${listItems.lastName} - ${listItems.userRole}</option>
							</c:if>
						</c:forEach>
					</select>
					<input type="hidden" name="hdnLev4Users" value="" id="hdnLev4Users" />
					<div class='taskButtons floatLft'>
						<input type='button' id="level4Add" value='Add User' class='add' disabled onclick="addUserToLevel(this);" />
					</div>
					<div class='taskButtons floatRht'>
						<input id="level4Remove" type='button' value='Remove User' class='remove' disabled
							onclick="removeUserFrmLevel(this);enableremovebtn(this);" />
					</div>
				</div>
			</div>
		</div>
		<p></p>

		<%-- Button Wrapper --%>
		<div class="buttonholder">
			<input type="button" class="graybtutton" value="Cancel Changes"
				onclick="javascript:cancelClickedLevelUsers();" />
			<input type="button" value="Save" class="button"
				onclick="javascript:ajaxCallToSaveLevelUsers();" />
		</div>
	</div>
</div>
</div>