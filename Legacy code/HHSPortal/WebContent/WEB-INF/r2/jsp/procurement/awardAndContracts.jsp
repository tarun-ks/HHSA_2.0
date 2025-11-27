<%--
 *awardAndContracts.jsp
 *This screen allows the Agency user to see a list of providers that 
 *only have a Provider status of ‘Selected’ for the Procurement. 
  --%>
<%@page import="javax.portlet.PortletContext"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages" />
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/award.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<%-- Start : Changes in R5 --%>
<style>
.popupPadding{
	padding: 0 10px 0 10px;
}
.btncancelAll{
	padding:0.25em;
	cursor:pointer;
	position: relative;
	margin-bottom: 2px;
}
</style>
<%-- End : Changes in R5 --%>
<%-- View award documents --%>
<portlet:renderURL var='viewAwardDocuments' id='viewAwardDocuments'
	escapeXml='false'>
	<portlet:param name="action" value="selectionDetail" />
	<portlet:param name="render_action" value="viewSelectionDetails" />
</portlet:renderURL>
<input id="screenLockedFlag" type="hidden" value="${accessScreenEnable}" />
<input type='hidden' value='${viewAwardDocuments}'
	id='hiddenViewAwardDocuments' />
<%--  View award documents --%>
<%-- R5 change starts --%>
<portlet:actionURL var="cancelAllAward" escapeXml="false">
<portlet:param name="action" value="awardContract" />
<portlet:param name="submit_action" value="cancelAllAward" />
</portlet:actionURL>
<input type='hidden' value='${cancelAllAward}'
	id='hiddenCancelAllAwardUrl' />
<%-- R5 change ends --%>
<%-- Cancel Award --%>
<portlet:resourceURL var='cancelAward' id='cancelAwardOverlay'
	escapeXml='false'>
	<portlet:param name="action" value="awardContract" />
</portlet:resourceURL>
<input type='hidden' value='${cancelAward}'
	id='hiddenCancelAwardOverlayUrl' />
<div class="alert-box alert-box-cancelAward">
<div class='tabularCustomHead'>Cancel Award</div>
<a href="javascript:void(0);" class="exit-panel cancel-Award">&nbsp;</a>
<div id="requestCancel"></div>
</div>
<%-- Cancel Award --%>
<%-- view APT PROGESS--%>
<portlet:resourceURL var='viewAptInformation' id='viewAptInformation'
	escapeXml='false'>
	<portlet:param name="action" value="awardContract" />
</portlet:resourceURL>
<input type='hidden' value='${viewAptInformation}'
	id='viewAptInformation' />
<div class="overlay"></div>
<div class="alert-box alert-box-viewAptProgress">
<div class='tabularCustomHead'>View APT Progress</div>
<a href="javascript:void(0);" class="exit-panel viewAptProgress">&nbsp;</a>
<div id="viewAptProgress"></div>
</div>
<%-- //view APT PROGESS--%>
<%-- assign award Epin --%>
<%-- Start : Changes in R5 --%>
<portlet:renderURL var="redirectURL" escapeXml="false">
	<portlet:param name="procurementId" value="${procurementId}"/>
	<portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}"/>
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}"/>
	<portlet:param name="topLevelFromRequest" value="AwardsandContracts"/>
	<portlet:param name="midLevelFromRequest" value="AwardsandContractsScreen"/>
	<portlet:param name="ES" value="0"/>
	<portlet:param name="paramValue" value="cancelAward"/>
	<portlet:param name="render_action" value="awardsAndContracts"/>
	<portlet:param name="action" value="awardContract"/>
</portlet:renderURL>
<%-- End : Changes in R5 --%>
<portlet:resourceURL var='assignAwardEpin' id='assignAwardEpin'
	escapeXml='false'>
	<portlet:param name="action" value="awardContract" />
</portlet:resourceURL>
<input type='hidden' value='${assignAwardEpin}'
	id='hiddenAssignAwardEpinOverlayContentUrl' />
<div class="alert-box alert-box-assignAwardPIN">
<div class='tabularCustomHead'>Assign APT Award E-PIN</div>
<a href="javascript:void(0);" class="exit-panel cancel-assignAwardPIN">&nbsp;</a>
<div id="assignAwardPIN"></div>
</div>
<%-- assign award Epin --%>

<nav:navigationSM screenName="AwardsandContractsScreen">
	<portlet:actionURL var="pagingURL" escapeXml="false">
		<portlet:param name="action" value="awardContract" />
		<portlet:param name="submit_action" value="pagingContracts" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:actionURL>

	<form:form id="awardAndContracts" action="${pagingURL}" method="post"
		commandName="AuthenticationBean" name="awardAndContracts">

		<input type="hidden" name="nextPage" value="" id="nextPage" />
		<input type="hidden" name="procurementId" value="${procurementId}" id="procurementId" />
		<%-- Code updated for R4 Starts --%>
		<input type = "hidden" value="${evaluationPoolMappingId}" name="evaluationPoolMappingId"/>
		<input type = "hidden" value="${evaluationGroupId}" name="evaluationGroupId"/>
		<%-- Code updated for R4 Ends --%>
		<input type="hidden" id="topLevelFromRequest" name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<input type="hidden" id="midLevelFromRequest" name="midLevelFromRequest" value="${midLevelFromRequest}" />
		<%-- Code updated for R4 Starts --%>
		<%-- R5 change starts --%>
		<input type="hidden" value="${redirectURL}" id="redirectURL"/>
		<input type="hidden" value="" id="action_redirect"/>
		<input type="hidden" id ="competitionPoolTitle" name="competitionPoolTitle" value="${groupTitleMap['COMPETITION_POOL_TITLE']}" />
		<%-- R5 change ends --%>
		<div id='tabs-container'><d:content
			isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
			<c:if test="${fn:length(competitionPoolList) > 1}">
				<select id="compPoolDropDown" class="floatRht" >
					<option value="-1">Select Competition Pool</option>
					<c:forEach var="entry" items="${competitionPoolList}">
						<option value="${entry['EVALUATION_POOL_MAPPING_ID']}">${entry['COMPETITION_POOL_TITLE']}</option>
		            </c:forEach>
				</select>
			</c:if>
			
			<div class='clear'></div>
			<h2>Awards and Contracts
				<a id="returnAwardContractSummary" class="floatRht returnButton" href="#">Awards and Contracts Summary</a>
			</h2>
			<%-- Code updated for R4 Ends --%>
			<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
			<d:content section="${helpIconProvider}">
				<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
				<d:content section="${helpIconProvider}">
					<%-- Code updated for R4 Starts --%>
					<div id="helpIcon" class="iconQuestion"><a
						href="javascript:void(0);" id="helpIconId" title="Need Help?"
						onclick="smFinancePageSpecificHelp();"></a></div>
					<input type="hidden" id="screenName" value="Awards and Contracts"
						name="screenName" />
				<%-- Code updated for R4 Ends --%>
				</d:content>
				<input type="hidden" id="screenName" value="Awards & Contracts"
					name="screenName" />
			</d:content>
			<div class='hr'></div>
			
			<p>Please use the chart below to view and track the status of
			awards and contracts.</p>
			
			<c:if test="${message ne null}">
				<div id="messagediv" class="${messageType}" style="display: block;">
				${message} <img onclick="showMe('messagediv', this)"
					class="message-close" id="box"
					src="../framework/skins/hhsa/images/iconClose.jpg"></div>
			</c:if>
			<c:if
				test="${noProposalForAwardsFlag ne null and noProposalForAwardsFlag eq 'YES'}">
				<div class='infoMessage' id="messagediv" style="display: block">No
				proposals were selected for award for this procurement’s competition pool.</div>
			</c:if>
			<c:if
				test="${awardList ne null and awardList[0].contractTypeId eq 5}">
				<div id="messagediv" class="infoMessage" style="display: block;">The
				awards/contracts listed below will not use the HHS Accelerator
				system for financial processing.</div>
			</c:if>
			<br />
			<c:if
				test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
				<c:set var="lockAccess">
					<fmt:message key='SCREEN_LOCKED' />
				</c:set>
				<div class="lockMessage" style="display: block">${lockedByUser}
				${lockAccess}</div>
			</c:if>
<%-- Code updated for R4 Starts --%>
			<div class="formcontainer">
			<c:if
				test="${procurementBean.isOpenEndedRFP eq '1'}">
				<div class="row"><span class="label">Evaluation Group:</span>
				<span class="formfield">${groupTitleMap['EVALUATION_GROUP_TITLE']}</span>
				</div>
				<div class="row"><span class="label">Closing Date:</span> <span
					class="formfield">${groupTitleMap['SUBMISSION_CLOSE_DATE']}</span>
				</div>
			</c:if>
			<div class="row"><span class="label">Competition Pool:</span> <span
				class="formfield">${groupTitleMap['COMPETITION_POOL_TITLE']}</span>
			</div>
			<!-- R5 changes starts -->
			<c:if test="${awardList ne null and (awardList[0].awardId eq '34')}">
			<div class="row">
				<span class="label">With Financials?:</span>
				<span class="formfield"><c:choose><c:when  test="${awardList[0].contractTypeId eq 1}">Yes </c:when><c:otherwise>No</c:otherwise></c:choose></span>
			</div>
			<div class="row">
				<span class="label">Is Award Amount Negotiable?:</span>
				<span class="formfield"><c:choose>
								<c:when test="${(awardList[0].awardNegotiationFlag eq '1')}">Yes </c:when>
								<c:otherwise>No</c:otherwise>
							</c:choose>
				</span>
			</div>
			</c:if>
			<%-- Defect 7203 change starts --%>
			<c:set var="sectionViewProgress" value="cancelAllAwardVisibility"/>
			<c:if test="${fn:length(awardList) ne 0 and cancelAwardsCount eq 0}">
			<d:content section="${sectionViewProgress}">
			<%-- Defect 7203 change ends --%>
			<div >
			<input type="button" value=" Cancel All Awards" class="cancelGreen right btncancelAll" onclick="cancelAllAwards()">
			<div>
			</d:content>
			</c:if>
			<!-- R5 changes ends -->
			</div>
<%-- Code updated for R4 Ends --%>
			<div class="tabularWrapper"><st:table
				objectName="awardList" cssClass="heading" alternateCss1="evenRows"
				alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
				<st:property headingName="Provider Name" columnName="providerName"
					size="20%" align="center">
				</st:property>
				<st:property headingName="Award E-PIN" columnName="epin" size="15%"
					align="center">
				</st:property>
				<st:property headingName="CT&#35;" columnName="contractNumber"
					size="15%" align="center">
				</st:property>
				<st:property headingName="Amount ($)" columnName="awardAmount"
					size="15%" align="center">
				<st:extension
						decoratorClass="com.nyc.hhs.frameworks.grid.AwardsActionExtension" />
				</st:property>
				<st:property headingName="Contract Status" columnName="contractStatus"
					size="15%" align="center">
				</st:property>
				<st:property headingName="Action" columnName="actions" size="20%"
					align="center">
					<st:extension
						decoratorClass="com.nyc.hhs.frameworks.grid.AwardsActionExtension" />
				</st:property>
			</st:table></div>
			<c:if test="${awardList eq null or fn:length(awardList) eq 0}">
				<div class="noRecord">There are currently no approved awards for this competition pool.</div>
			</c:if>

		</d:content>
		<div id="pendingAwardTipDiv"><p> <br></span><span class="red-ex-mark"/>= Final award amount pending</p></div>
		</div>
		<!-- R5 change starts -->
		<div class="alert-box alert-box-cancelAllAwards skipElementsInCompare">
			<div class='tabularCustomHead'>Cancel All Awards</div>
			<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			<div id="requestCancel" class="popupPadding">
				<h2>Cancel All Awards</h2>
				<div class='hr'></div>
		
				<div class="failed" id="errorMsg"></div>
				<p>Are you sure you want to cancel all awards for this competition pool?</p>
				<p>
					Following cancellation, all existing workflows for this competition pool will be<br>
					terminated.The Award Approval Task will be regenerated with the same proposal selections.
				</p>
				<div class="formcontainer">
				<div class="row">
					<span class="label" >Procurement:</span> <span class="formfield" id="providerName">${awardList[0].procurementTitle}</span>
				</div>
				<div class="row">
					<span class="label" >Competition Pool:</span> <span class="formfield" id="propsalTitle">${groupTitleMap['COMPETITION_POOL_TITLE']}</span>
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
					<div class="row">
						<span class='label clearLabel autoWidth textAlignLeft'> <input name=""
							type="checkbox" id='chkSubmitCBForm' /> <label
							for='chkSubmitCBForm'>Yes, I understand that cancelling awards terminates the current workflows and will retrigger award approval.</label>
						</span> <span class="error"></span>
					</div>
					<div class="row" id="usernameDiv">
						<span class="label"> <label class="required">*</label><label
							for='txtSubmitCBUserName'>User Name:</label>
						</span> <span class="formfield"> <input type="text"
							class='proposalConfigDrpdwn' id="txtSubmitCBUserName"
							name='userName' maxlength="128" value=""/>
						</span> <span class="error" style="padding-left: 5px;" id="usernamespan"></span>
					</div>
					<div class="row" id="passwordDiv">
						<span class="label"> <label class="required">*</label><label
							for='txtSubmitCBPassword'>Password:</label>
						</span> <span class="formfield"> <input type="password"
							class='proposalConfigDrpdwn' name='password'
							id="txtSubmitCBPassword" autocomplete="off" />
						</span> 
						<span class="error" style="padding-left: 5px;" id="passwordspan"></span>
						</div>
				</div>
				<div class="buttonholder">
					<input type="button" class="graybtutton" title=""
						value="No, do NOT Cancel ALL Awards" id="btnNotSubmitCB" onclick="cancelOverLay()" /> <input
						type="button" class="button" title="" value="Yes, Cancel ALL Awards"
						id="btnSubmitCB" onclick="finishTask()" />
				</div>
			</div>
		</div>
		<!-- R5 change starts -->
	</form:form>
</nav:navigationSM>
<div class="overlay"></div>
