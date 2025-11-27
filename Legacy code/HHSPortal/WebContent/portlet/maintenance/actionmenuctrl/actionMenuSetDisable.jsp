<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<div>
<h4 class='generateListPanel agencyGreyTitle'><label id="levelofRevText" class="bold"></label></h4>

<div class='agencySettingWrapper'>
	<div class='lftContainer'>
		<h3>Available Action</h3>
		<c:set var="loAgencySettingsBean" value="${AgencySettingsBean}"></c:set>
		<div class='generateListPanel agencyGreyTitle alignCenter'>Active Action List</div>
		<select id="allActions" name="agencyActions" size="10" multiple="multiple" onchange="enableDisableAddbtns(this);">
			<c:forEach var="listItems" items="${loAgencySettingsBean.actionMenuList}">
			    <c:if test="${listItems.actionStatus eq '1'}">
							<option value="${listItems.actionInx}">${listItems.actionName}	</option>
				</c:if>
			</c:forEach>
		</select> 
		<input type="hidden" name="hdnAllUsers" value="" id="hdnAllUsers" />
		<input type="hidden" name="hdnAgencyId" value="" id="hdnAgencyId" />
	</div>

	<div class='rhtContainer'>
		<h3>Unavailable Action</h3>
		<div class='generateListPanel agencyGreyTitle alignCenter'>Inactive Action List</div>
		<select id="inactiveActions" name="inactiveActions" multiple="multiple" onchange="enableremovebtn(this);">
			<c:forEach var="listItems" items="${loAgencySettingsBean.actionMenuList}">
				<c:if test="${listItems.actionStatus eq '0'}">
							<option value="${listItems.actionInx}">${listItems.actionName}	</option>
				</c:if>
			</c:forEach>
		</select> <input type="hidden" name="hdnLev1Users" value="" id="hdnLev1Users" />
		<div class='taskButtons floatLft'>
			<input type='button' id="level1Add" disabled value='Add Action' onclick="addUserToLevel(this);" />
		</div>
		<div class='taskButtons floatRht'>
			<input id="level1Remove" type='button' value='Remove Action' disabled
				onclick="removeUserFrmLevel(this);enableremovebtn(this);" />
		</div>

		<p></p>

		<%-- Button Wrapper --%>
		<div class="buttonholder">
			<input type="button" class="graybtutton" value="Cancel Changes" onclick="cancelClickedActions();" />
			<input type="button" value="Save" class="button" onclick="ajaxCallToSaveActionMenuSetting();" />
		</div>
	</div>
</div>
</div>