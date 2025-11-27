<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/cancelBudgetModification.js"></script>
<%-- Overlay Popup Starts --%>
<portlet:resourceURL var="cancelBudgetModification" id="cancelBudgetModification" escapeXml="false">
</portlet:resourceURL>
<style>
.alert-box-proposal-comments{
	width:540px !important
}
</style>
<div class="content">
<div class='tabularCustomHead'><span id="contractTypeId">Confirm Budget Modification Cancellation</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>
<div class="tabularContainer">
<h2>Cancel Budget Modification</h2>
<div class='hr'></div>
<c:if
	test="${accessScreenEnable eq false}">
	<c:set var="readOnlyPageAttribute" value="true"></c:set>
	<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this Budget Modification. This record cannot be cancelled.</div>
</c:if>

<d:content isReadOnly="${readOnlyPageAttribute}">
<form:form id="cancelBudgetModificationForm" action="" method="post"
		commandName="AuthenticationBean" name="cancelBudgetModificationForm">
	<p>Are you sure you want to cancel this Budget Modification? Once a modification is cancelled, no further actions can be taken on the Modification. Please fill in the required information and then click on the "Yes, cancel this Modification" button to continue. </p>
	 <div id="ErrorDiv" class="failed"  > </div>
	<p><label class="required">*</label>Indicates a required field.</p>		
	<div class="formcontainer" id="cancelBudgetModification">
		
		<c:choose>
		<c:when test="${accessScreenEnable eq false}">
		<p>
		<input type="checkbox" id='chkCancelBudgetModification'  disabled='disabled'"/>
		<label for='chkCancelBudgetModification'>I agree to cancel this Budget Modification.</label></p>
		</c:when>
		<c:otherwise>
		<p>
		<input type="checkbox" id='chkCancelBudgetModification' onclick="hideUnhideUsername(this);"/>
		<label for='chkCancelBudgetModification'>I agree to cancel this Budget Modification.</label></p>
		</c:otherwise>
		</c:choose>
	
		<div>&nbsp;</div>
		<div id="authenticate">
			<div class="row" id="usernameDiv">
				<span class="label"><label class="required">*</label><label for='userName'>User Name:</label></span>
				<span class="formfield">
					<form:input path="userName" cssClass="select" id="userName" maxlength="128"/>
				</span>
				<span class="error">
				</span>
			</div>
			<div class="row" id="passwordDiv">
				<span class="label"><label class="required">*</label><label for='password'>Password:</label></span>
				<span class="formfield">
					<form:password path="password" cssClass="select" id="password" autocomplete="off"/>
				</span>
				<span class="error">
				</span>
			</div>
		</div>
	</div>
		
    <div class="buttonholder nowrap">
    	<input type="button" class="graybtutton" title="" id="doNotCancelModification" value="No, do NOT cancel this Modification" onclick="clearAndCloseOverLay();" />
    	<input type="submit" class="" title="" id="yesCancelModification" value="Yes, cancel this Modification"/>
    </div>
	 <input type = "hidden" name = "cancelModificationBudgetId" value = ${cancelModificationBudgetId}>   
	 <input type = 'hidden' value='${cancelBudgetModification}' id='cancelBudgetModificationUrl'/>
  </form:form>
   </d:content>
</div>
</div>
  
