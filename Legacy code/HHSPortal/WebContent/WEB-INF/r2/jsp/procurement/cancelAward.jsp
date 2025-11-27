<%-- This jsp is used to create cancel award popup --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/cancelAward.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<%-- Overlay Popup Starts --%>
<portlet:actionURL var="cancelAwardUrl" escapeXml="false">
	<portlet:param name="submit_action" value="cancelAward"/>
	<portlet:param name="action" value="awardContract"/>
</portlet:actionURL>
<portlet:renderURL var="redirectURL" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}"/>
	<%-- Code updated for R4 Starts --%>
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}"/>
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}"/>
	<%-- Code updated for R4 Ends --%>
	<portlet:param name="topLevelFromRequest" value="AwardsandContracts"/>
	<portlet:param name="midLevelFromRequest" value="AwardsandContractsScreen"/>
	<portlet:param name="ES" value="0"/>
	<portlet:param name="paramValue" value="cancelAward"/>
	<portlet:param name="render_action" value="awardsAndContracts"/>
	<portlet:param name="action" value="awardContract"/>
</portlet:renderURL>
<form:form id="cancelAwardForm" action="${cancelAwardUrl}" method="post"
		commandName="AuthenticationBean" name="cancelAwardForm">
		<input type="hidden" value="${redirectURL}" id="redirectURL"/>
		<input type="hidden" value="${contractID}" id="contractID" name ="contractID"/>
		<input type="hidden" value="${organizationId}" id="organizationId" name ="organizationId"/>
		<input type="hidden" value="${awardId}" id="awardId" name ="awardId"/>
		<%-- Code updated for R4 Starts --%>
		<input type="hidden" value="${evaluationPoolMappingId}" id="evaluationPoolMappingId" name ="evaluationPoolMappingId"/>
	<%-- Code updated for R4 Ends --%>
	<div class='tabularContainer'>
	<h2>Cancel Award</h2>
	<div class='hr'></div>
	<p>Are you sure you want to cancel this Award?</p>
	<p>Cancelling this award will mark the proposal as Not Selected. After cancelling this award, make any necessary changes on the Evaluation Results page and click the Update Results button to submit changes to HHS Accelerator.
	</p>
	
	<div class="formcontainer">
		<div class="row">
			 <span class="label">Provider Name:</span>
				<span class="formfield">
					${awardBean.providerName}
				</span>
		</div>
		<div class="row">
			 <span class="label">Award E-PIN:</span>
				<span class="formfield">
					${awardBean.epin}
				</span>
		</div>
		<div class="row">
			 <span class="label">CT#:</span>
				<span class="formfield">
					${awardBean.ctNumber}
				</span>
		</div>
		<div class="row">
			 <span class="label">Award Amount($):</span>
				<span class="formfield awardAmt">
					${awardBean.awardAmount}
				</span>
		</div>
		<div class="row">
			<span class="label">Status:</span>
			<span class="formfield">
				  ${awardBean.status}	
			</span>
		</div>
	</div>
	
	<div id="errorPlacementWrapper"> 
	<c:if test="${message ne null}">
				<div class="${messageType}" id="errorPlacement" style="display:block">${message} 
				<img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('errorPlacementWrapper', this)"/>
				</div>
			</c:if>
		</div>
	<div class="formcontainer" id="cancelAward">
		<p><input type="checkbox" id='chkCancelAward' onclick="hideUnhideUsername(this);"/>
		<label for='chkCancelAward'>Yes, I understand that cancelling this award changes the provider&rsquo;s proposal status and requires an update to the Evaluation Results and Selections page.</label></p>
	<p>
			<input name="reuseEpin" type="checkbox" id='reuseEpin' value="Y"/>
			<label for='reuseEpin'>Reuse EPIN for this award.</label></span>
	</p>
    	<div>&nbsp;</div>
		<div id="authenticate">
			<div class="row" id="usernameDiv">
				<span class="label">User Name:</span>
				<span class="formfield">
					<form:input path="userName" cssClass="input" id="userName" autocomplete="off"/>
				</span>
				<span class="formfield error">
				</span>
			</div>
			<div class="row" id="passwordDiv">
				<span class="label">Password:</span>
				<span class="formfield">
					<form:password path="password" cssClass="input" id="password" autocomplete="off"/>
				</span>
				<span class="formfield error">
				</span>
			</div>
		</div>
	</div>
		
    <div class="buttonholder">
    	<input type="button" class="graybtutton" id="doNotCancelAward" value="No, do NOT Cancel this Award" onclick="cancelOverLay();" />
    	<input type="submit" class="" id="yesCancelAward" value="Yes, Cancel this Award"/>
    </div>
    </div>
    <form:hidden path="procurementId" id="procurementId" value="${procurementId}"/> 
    <%-- Code updated for R4 Starts --%>
    <input type="hidden" name="competitionPoolStatus" id="competitionPoolStatus" value="${competitionPoolStatus}"/>
 <%-- Code updated for R4 Ends --%>
 </form:form>
  <%-- Overlay Popup Ends --%>
  
