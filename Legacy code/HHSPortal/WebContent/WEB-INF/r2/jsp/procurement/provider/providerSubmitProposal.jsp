<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<nav:navigationSM screenName="SubmitProposal">
	<portlet:defineObjects />
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/submitproposal.js"></script>
	<portlet:actionURL var="submitProposalUrl" escapeXml="false">
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="submit_action" value="submitProposal" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}"/>
	</portlet:actionURL>
	
	<portlet:renderURL var="proposalSummaryUrl" escapeXml="false">
		<portlet:param name="topLevelFromRequest" value="ProposalSummary" />						
		<portlet:param name="action" value="propEval" />
		<portlet:param name="render_action" value="proposalSummary" />
		<portlet:param name="procurementId" value="${procurementId}" />
	</portlet:renderURL>
	<%-- Code updated for R4 Starts --%>
		  <%  if( ApplicationConstants.ROLE_STAFF.equalsIgnoreCase(session.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE).toString().trim())||
		        			ApplicationConstants.ROLE_ADMINISTRATOR_PROV_STAFF.equalsIgnoreCase(session.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE).toString().trim())) {%>
 <div class="failed break" style="display:block" id="error"><fmt:message key='ACCESS_ERROR_FOR_LEVEL1_USER'/></div>	
 <%} %>
 <%-- Start Release 5 user notification --%>
	<d:content readonlyRoles="staff,providerAdminStaff" readOnlyOrgType="provider_org" isReadOnly="">
	<%-- Code updated for R4 Ends --%>
	<div id='tabs-container' class='clearHeight'>
	<form:form id="submitProposalForm" action="${submitProposalUrl}" method="post" commandName="Authentication" name="submitProposalForm">
		<d:content isReadOnly="${(accessScreenEnable eq false) or (submitProposalReadonlyFlag eq true)}">
	<%-- End Release 5 user notification --%>
		<h2>
			<label class='floatLft'>
				Submit Proposal: 
					<label>${proposalTitle}</label>
			</label>
			<a id="returnProposalSummaryPage" class="floatRht returnButton" href="javascript:;">Proposal Summary</a>
		</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Submit Proposal" name="screenName"/>
		</d:content>
		<div class='hr'></div>
		<input type="hidden" id="proposalSummaryUrl" value="${proposalSummaryUrl}"/>
		<p>
			Please review the terms and conditions and the checkboxes below, 
			and enter your User Name and Password to submit your organization's Proposal.</p>
		<c:if test="${message ne null and information eq null}">
			<div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close" onclick="showMe('messagediv', this)"></div>
		</c:if>
		<c:if test="${information ne null}">
			<div class="infoMessage" id="messagediv" style="display:block">${information} <img
							src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
							class="message-close" onclick="showMe('messagediv', this)">
			</div>
		</c:if>
		
		<c:if test="${accessScreenEnable eq false}">
			<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
			<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
		</c:if>
		<div id="mymain">
			<div class="termspanel">
				<div>
			 		${displayTermsCondition}
			 	</div>
			</div>
			<p>
				<input name="Terms" type="checkbox" value="" id='chktermsNConditions' onclick="hideUnhideAuthenticateDiv();"/>
				<label for='chktermsNConditions'>I have read the Terms and Conditions and have reviewed the <a href="javascript:viewDocumentByType('Standard Contract');" class='link' >Standard Contract</a> and</label>  
				<br>
				<a href="javascript:viewDocumentByType('Appendix A');" class='link' >Appendix A - General Provisions Contracts for Consultants, Profession, Technical, Human and Client Services.</a>
			</p>
			<p>
				<input name="acknowledge" type="checkbox" value="" id='chkAcknowledge' onclick="hideUnhideAuthenticateDiv();"/>
				<label for='chkAcknowledge'>I acknowledge that I have reviewed the documents in the RFP Documents tab, including all Addenda to this Solicitation, if applicable. </label>  
			</p>
			<%-- Code updated for R4 Starts --%>
			<p>
				<input name="IranDivestmentAct" type="checkbox" value="" id='IranDivestmentAct' onclick="hideUnhideAuthenticateDiv();"/>
				<label for='IranDivestmentAct'>Compliance with Iran Divestment Act - Pursuant to General Municipal Law $103-9, which generally prohibits the City from entering into contracts with persons engaged in investment activities in the energy sector of Iran, the proposer submits the following certification:  By submission of this proposal, each proposer and each person signing on behalf of any proposer certifies, and in the case of a joint bid each party thereto certifies as to its own organization, under penalty of perjury, that to the best of its knowledge and belief, that each proposer is not on the list created pursuant to paragraph (b) of subdivision 3 of Section 165-a of the State Finance Law. </label>  
			</p>
			<%-- Code updated for R4 Ends --%>
			<div id="authenticate">
				<div class="formcontainer">
					 <div class="row">
					      <span class="label">User Name:</span>
					      <span class="formfield"> 
					      	<form:input path="userName" cssClass="input" id="userName" autocomplete="off"/>
					      </span>
					      <span class="formfield error">
					      	<form:errors path="userName" cssClass="ValidationError"></form:errors>
					      </span>
					 </div>
					  <div class="row">
					      <span class="label">Password:</span>
					      <span class="formfield"> 
					      	<form:password path="password" cssClass="input" id="password" autocomplete="off"/>
					      </span>
					      <span class="formfield error">
					      	<form:errors path="password" cssClass="ValidationError"></form:errors>
					      </span>
					  </div>
				 </div>
				 <div class="buttonholder"><input type="submit" value="Submit Proposal"/></div>
			 </div>
		</div>
		<input type="hidden" name="proposalId" value="${proposalId}"/>
		<input type="hidden" name="procurementId" value="${procurementId}"/>
		<input type="hidden" name="proposalTitle" value="${fn:escapeXml(proposalTitle)}"/>
		<input type="hidden" name="proposalStatus" value="${proposalStatus}"/>
		
		</d:content>
	</form:form>
	</div>
	</d:content>
	<input type="hidden" id="error" value="${error}"/>
	<div class="overlay"></div>
</nav:navigationSM>