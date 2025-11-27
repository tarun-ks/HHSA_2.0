<%--This jsp is used for S235 screen--%>
<%@ page import="javax.portlet.PortletContext"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="rule" uri="/WEB-INF/tld/rule-taglib.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<style>
.alertBoxAddSite .tabularContainer .formcontainer .row span.formfield {
    width: 48% !important 
}
.alertBoxAddSite .formcontainer label.error{
	float:left
}
</style>
<%--Navigation added on screen --%>
<nav:navigationSM screenName="ProposalDetails">
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/proposalDetails.js" charset="utf-8"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
	<portlet:actionURL var="saveProposalDetails" escapeXml="false">
		<portlet:param name="submit_action" value="saveProposalDetails"/>
		<portlet:param name="proposalId" value="${proposalId}"/>
		<portlet:param name="procurementId" value="${procurementId}"/>
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:actionURL>
	<portlet:actionURL var="nextProposalDetails" escapeXml="false">
		<portlet:param name="submit_action" value="nextProposalDetails"/>
		<portlet:param name="proposalId" value="${proposalId}"/>
		<portlet:param name="procurementId" value="${procurementId}"/>
		<portlet:param name="action" value="rfpRelease" />
		<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}" />
		<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}" />
	</portlet:actionURL>
	<portlet:renderURL var="proposalSummaryUrl" escapeXml="false">
		<portlet:param name="topLevelFromRequest" value="ProposalSummary" />						
		<portlet:param name="action" value="propEval" />
		<portlet:param name="render_action" value="proposalSummary" />
		<portlet:param name="procurementId" value="${procurementId}" />
	</portlet:renderURL>
	
	<portlet:resourceURL var='getMemberDetails' id='getMemberDetails' escapeXml='false'>
	</portlet:resourceURL>
	<portlet:resourceURL var="showProposalComments" id="showProposalComments" escapeXml="false">
		<portlet:param name="action" value="propEval" />	
		<portlet:param name="proposalId" value="${proposalId}"/>
	</portlet:resourceURL>
	<c:if test="${proposalDetailsReadonlyFlag eq true or ProposalDetailsBean.procurementStatus eq '8' or ProposalDetailsBean.procurementStatus eq '7'}">
		<c:set var="disableAll" value="true"></c:set>
	</c:if>
	<rule:Rule ruleId="showProposalCommentLinkPropDetalis" requestAttName="statusChannel">
		<c:set var="showCommentLink" value="true"></c:set>
	</rule:Rule>
	<input type="hidden" id="proposalSummaryUrl" value="${proposalSummaryUrl}"/>
	<input type="hidden" id="getMemberDetailsURL" value="${getMemberDetails}"/>
	<input type="hidden" id="nextProposalDetails" value="${nextProposalDetails}"/>
	<%-- Form Data Starts --%>
		<div id='tabs-container'>
		<form:form id="proposalDetailsForm1" name="proposalDetailsForm1" action="${saveProposalDetails}" method ="post" commandName="ProposalDetailsBean">
		<d:content isReadOnly="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)  }" >
		<form:hidden path="serviceUnitFlag" id="serviceUnitFlag"/>
		<form:hidden path="versionNoQuestion" id="versionNoQuestion"/>
		<c:set var="readOnlyValue" value=""></c:set>
		<c:if test="${readOnlySection or ProposalDetailsBean.procurementStatus eq '8' or ProposalDetailsBean.procurementStatus eq '7'}">
			<c:set var="readOnlyValue" value="cssClass='readOnlyValue' class='readOnlyValue'"></c:set>
		</c:if>
			<h2>
				<label class='floatLft'>
				Proposal Details:
				<label>
					<c:choose>
						<c:when test="${ProposalDetailsBean.proposalTitle eq null or ProposalDetailsBean.proposalTitle eq ''}">
							Untitled Proposal
						</c:when>
						<c:otherwise>
							${ProposalDetailsBean.proposalTitle}
						</c:otherwise>
					</c:choose>
				</label>
				</label>
				<a id="returnProposalSummaryPage" class="floatRht returnButton" href="#">Proposal Summary</a>
			</h2>
			<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
				<d:content section="${helpIconProvider}">
					<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
					<input type="hidden" id="screenName" value="Proposal Details" name="screenName"/>
				</d:content>	
			<div class='hr'></div>
			<div id="noSiteMessage">
				<form:errors path="siteDetailsList" cssClass="ValidationError"></form:errors>
			</div>
			<c:if test="${message ne null}">
				<div class="${messageType}" id="infoMessageDiv" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" onclick="showMe('infoMessageDiv', this)">
				</div>
			</c:if>
			<c:if test="${information ne null}">
				<div class="infoMessage" id="infoMessageDiv" style="display:block">${information} <img
								src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
								class="message-close" onclick="showMe('infoMessageDiv', this)">
							</div>
			</c:if>
			<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
				<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
				<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
			</c:if>
			<p>Please enter requested information in the sections below.
			<br />
			<span class="required">*</span>Indicates required fields</p>
			<c:if test="${showCommentLink eq 'true'}">
				<p><a href='javascript:;' id="showProposalComment" class='iconComments'>Show Proposal Comments</a></p>
			</c:if>

			<input type = 'hidden' value='${showProposalComments}' id='showProposalCommentsResourceUrl'/>
		
			<h3>Basic Information</h3>
			<div class="formcontainer">
				 <div class="row">
					<span class="label"><span class="required">*</span>Proposal Title:</span>
					<span class="formfield">
						<form:input path="proposalTitle" id="proposalTitle" cssClass="input" maxlength="90" disabled="${disableAll}" ${readOnlyValue}/>
					</span>
					<span class="formfield error">
						<form:errors path="proposalTitle" cssClass="ValidationError"></form:errors>
					</span>
				</div>
				<%-- Code updated for R4 Starts --%>
				<div class="row">
					<span class="label"><span class="required">*</span>Competition Pool:</span>
					<span class="formfield">
						<c:choose>
							<c:when test="${fn:length(selectedPool) > 1}">
								<form:select path="competitionPool" id="competitionPool" cssClass="input" disabled="${disableAll}" ${readOnlyValue}>
									<form:option id="-1" value=""> </form:option>
									<c:forEach var="selectedPool" items="${selectedPool}">
										<c:choose>
											<c:when test="${ProposalDetailsBean.competitionPool eq selectedPool.COMPETITION_POOL_ID}">
												<option value="${selectedPool.COMPETITION_POOL_ID}" selected='selected'>${selectedPool.COMPETITION_POOL_TITLE}</option>
											</c:when>
											<c:otherwise>
												<option value="${selectedPool.COMPETITION_POOL_ID}">${selectedPool.COMPETITION_POOL_TITLE}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</form:select>
							</c:when>
							<c:otherwise>
								<form:select path="competitionPool" id="competitionPool" cssClass="input" disabled="true">
									<form:option value="${selectedPool[0].COMPETITION_POOL_ID}">${selectedPool[0].COMPETITION_POOL_TITLE}</form:option>
								</form:select>
								<form:hidden path="hiddenCompPoolId" value="${selectedPool[0].COMPETITION_POOL_ID}"/>
							</c:otherwise>
						</c:choose>
					</span>
					<span class="formfield error">
						<form:errors path="competitionPool" cssClass="ValidationError"></form:errors>
					</span>
				</div>
			<%-- Code updated for R4 Ends --%>
			</div>
			
			<h3>Provider Contact</h3>
			<div class="formcontainer">
				<div class="row">
					<span class="label"><span class="required">*</span>Select a member from your organization:</span>
					<span class="formfield">
						<form:select path="providerContactId" cssClass="input" id="providerContactId" title="To add a new member, click the Organization Information button above and go to the Members & Users tab." disabled="${disableAll}" ${readOnlyValue}>
							<form:option id="-1" value=""> </form:option>
							<c:forEach var="loOrgMemList" items="${loOrgMemList}">
								<c:set var='toShow'>false</c:set>
								<c:choose>
									<c:when test="${(ProposalDetailsBean.proposalStatus eq 17 or ProposalDetailsBean.proposalStatus eq 19) and ProposalDetailsBean.providerContactId eq loOrgMemList.STAFF_ID}">
										<c:if test="${loOrgMemList.MEMBER_STATUS eq 'Active' and loOrgMemList.USER_STATUS eq 'Yes' and loOrgMemList.ACTIVE_FLAG eq 'Yes'}">
											<c:set var='toShow'>true</c:set>
										</c:if>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${ProposalDetailsBean.providerContactId eq loOrgMemList.STAFF_ID}">
												<c:set var='toShow'>true</c:set>
											</c:when>
											<c:otherwise>
												<c:if test="${loOrgMemList.MEMBER_STATUS eq 'Active' and loOrgMemList.USER_STATUS eq 'Yes' and loOrgMemList.ACTIVE_FLAG eq 'Yes'}">
													<c:set var='toShow'>true</c:set>
												</c:if>											
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
								<c:if test="${toShow eq true}">
									<c:choose>
										<c:when test="${ProposalDetailsBean.providerContactId eq loOrgMemList.STAFF_ID}">
											<c:set var="toShowData">true</c:set>
											<option value="${loOrgMemList.STAFF_ID}" selected='selected'>${loOrgMemList.USER_NAME}</option>
										</c:when>
										<c:otherwise>
											<option value="${loOrgMemList.STAFF_ID}">${loOrgMemList.USER_NAME}</option>
										</c:otherwise>
									</c:choose>
								</c:if>
							</c:forEach>
						</form:select>
					</span>
					<span class="formfield error">
						<form:errors path="providerContactId" cssClass="ValidationError"></form:errors>
					</span>
				</div>
				<div class="row">
					<span class="label">Name:</span>
					<span class="formfield">
						<c:choose>
							<c:when test="${toShowData eq true}">
								<form:input path="providerName" cssClass="input readonly" readonly="readonly"  id="providerName" title="To add a new member, click the Organization Information button above and go to the Members & Users tab."/>
							</c:when>
							<c:otherwise>
								<input class="input readonly" type="text" readonly="readonly"  id="providerName" title="To add a new member, click the Organization Information button above and go to the Members & Users tab."/>
							</c:otherwise>
						</c:choose>
					</span>
				</div>
				<div class="row">
					<span class="label">Office Title:</span>
					<span class="formfield">
						<c:choose>
							<c:when test="${toShowData eq true}">
								<form:input path="providerOfficeTitle" id="providerOfficeTitle" ${readOnlyValue} cssClass="input readonly" readonly="readonly" title="To add a new member, click the Organization Information button above and go to the Members & Users tab."/>
							</c:when>
							<c:otherwise>
								<input id="providerOfficeTitle" type="text" ${readOnlyValue} class="input readonly" readonly="readonly" title="To add a new member, click the Organization Information button above and go to the Members & Users tab."/>
							</c:otherwise>
						</c:choose>
					</span>
				</div>
				<div class="row">
					<span class="label">Email Address:</span>
					<span class="formfield">
						<c:choose>
							<c:when test="${toShowData eq true}">
								<form:input path="providerEmailId" id="providerEmailId" ${readOnlyValue} cssClass="input readonly" readonly="readonly" title="To add a new member, click the Organization Information button above and go to the Members & Users tab."/>
							</c:when>
							<c:otherwise>
								<input type="text" id="providerEmailId" ${readOnlyValue} class="input readonly" readonly="readonly" title="To add a new member, click the Organization Information button above and go to the Members & Users tab."/>
							</c:otherwise>
						</c:choose>
					</span>
				</div>
				<div class="row">
					<span class="label">Phone:</span>
					<span class="formfield">
						<c:choose>
							<c:when test="${toShowData eq true}">
								<form:input path="providerPhone" id="providerPhone" ${readOnlyValue} cssClass="input readonly" readonly="readonly" title="To add a new member, click the Organization Information button above and go to the Members & Users tab."/>
							</c:when>
							<c:otherwise>
								<input type="text" id="providerPhone" ${readOnlyValue} cssClass="input readonly" readonly="readonly" title="To add a new member, click the Organization Information button above and go to the Members & Users tab."/>
							</c:otherwise>
						</c:choose>
					</span>
				</div>
			</div>
			<h3 title="Service Unit refers to number of proposed individuals served or deliverables provided (e.g. Clients, cases, rooms, beds or other measure). Refer to RFP documents for clarification.">Service Unit</h3>
			<div class="formcontainer">
				<c:if test="${ProposalDetailsBean.serviceUnitFlag eq '1'}">
					<div class="row">
						<span class="label"><span class="required">*</span>Total Number of Service Units:</span>
						<span class="formfield">
							<form:input path="totalNumberOfService" ${readOnlyValue} maxlength="5" id="totalNumberOfService" cssClass="input" title="Service Unit refers to number of proposed individuals served or deliverables provided (e.g., clients, cases, rooms, beds or other measure). Refer to RFP documents for clarification."  disabled="${disableAll}"/>
						</span>
						<span class="formfield error">
							<form:errors path="totalNumberOfService" cssClass="ValidationError"></form:errors>
						</span>
					</div>
				</c:if>
				<div class="row">
					<span class="label"><span class="required">*</span>Total Funding Request($):</span>
					<span class="formfield">
						<form:input path="totalFundingRequest" ${readOnlyValue} id="totalFundingRequest" cssClass="input"  disabled="${disableAll}"/>
					</span>
					<span class="formfield error">
						<form:errors path="totalFundingRequest" cssClass="ValidationError"></form:errors>
					</span>
				</div>
				<c:if test="${ProposalDetailsBean.serviceUnitFlag eq '1'}">
					<div class="row">
						<span class="label"><span class="required">*</span>Cost per Service Unit($/unit):</span>
						<span class="formfield">
							<form:input path="costPerUnit" id="costPerUnit" cssClass="input readonly" readonly="readonly"/>
						</span>
					</div>
				</c:if>
			</div>
			<c:set var="seqNo">
				<fmt:formatNumber type="number" groupingUsed="false" value="0"/>
			</c:set>
			<h3>Questions</h3>
			<div class="formcontainer">
				<c:forEach var="questionIterator" items="${ProposalDetailsBean.questionAnswerBeanList}" varStatus="item">
					<div class="row">
						<span class="label"><span class="required">*</span>${questionIterator.questionText}</span>
						<c:set var="seqNo" value="${seqNo+1}">
						</c:set> 
						<span class="formfield">
							<form:hidden path="questionAnswerBeanList[${seqNo - 1}].questionSeqNo"/>
							<form:hidden path="questionAnswerBeanList[${seqNo - 1}].procurementQnId"/>
							<form:input path="questionAnswerBeanList[${seqNo - 1}].answerText" maxlength="250" id="answerText${item.count}" cssClass="input" disabled="${disableAll}"/>
						</span>
						<span class="formfield error">
							<form:errors path="questionAnswerBeanList[${seqNo - 1}].answerText" cssClass="ValidationError"></form:errors>
						</span>
					</div>
				</c:forEach>
			</div>
			<h3>Service Site Information</h3>
			<p style="padding:0px!important;">Please enter an address for each site where your organization proposes to deliver services.</p>
			<c:if test="${disableAll ne 'true'}">
				<div class="buttonholder">
					<input type="button" ${readOnlyValue} value="+ Add Site" id='addSiteButton' />
				</div>
			</c:if>
			<div class="tabularWrapper">
					<table width="100%" cellspacing='0' cellpadding='0' border="1" id="siteDetailTable">
						<tr>
							<th>Site Name</th>
							<th>Address 1</th>
							<th>Address 2</th>
							<th>City</th>
							<th>State</th>
							<th>Zip Code</th>
							<c:if test="${disableAll ne 'true'}">
								<th>Action</th>
							</c:if>
						</tr>
						<c:choose>
							<c:when test="${empty ProposalDetailsBean.siteDetailsList}">
								<tr id="noSite">
									<td colspan="7">No sites have been entered...</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:forEach var="siteIterator" items="${ProposalDetailsBean.siteDetailsList}" varStatus="item">
									<tr id="trId${item.index}">
										<td>${siteIterator.siteName}</td>
										<td>${siteIterator.address1}</td>
										<td>${siteIterator.address2}</td>
										<td>${siteIterator.city}</td>
										<td>${siteIterator.state}</td>
										<td>${siteIterator.zipCode}</td>
										<c:if test="${disableAll ne 'true'}">
											<td>
												<select id="action${item.index}" class="siteAction">
													<option value="0">I need to... </option>
													<option value="1">Edit Site</option>
													<option value="2">Delete Site</option>
												</select>
												<form:hidden path="siteDetailsList[${item.index}].siteName"/>
												<form:hidden path="siteDetailsList[${item.index}].address1"/>
												<form:hidden path="siteDetailsList[${item.index}].address2"/>
												<form:hidden path="siteDetailsList[${item.index}].city"/>
												<form:hidden path="siteDetailsList[${item.index}].state"/>
												<form:hidden path="siteDetailsList[${item.index}].zipCode"/>
												<form:hidden path="siteDetailsList[${item.index}].actionTaken"/>
												<form:hidden path="siteDetailsList[${item.index}].proposalSiteId"/>
												<form:hidden path="siteDetailsList[${item.index}].addressRelatedData"/>
											</td>
										</c:if>
									</tr>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</table>
				</div>
				<div class="buttonholder">
					<c:choose>
						<c:when test="${disableAll ne 'true'}">
							<input type="hidden" id="saveType" name="saveType"/>
							<input type="submit" value="Save"  id="saveButton"/>
							<input type="submit" value="Save &amp; Next" id="saveNextButton"/>
						</c:when>
						<c:otherwise>
							<input type="button" value="Next"  id="nextButton" />
						</c:otherwise>
					</c:choose>
				</div>
				</d:content>
		</form:form>
	</div>
	
	
	<div class="overlay"></div>
	<%-- Overlay Pop up Starts --%>
	<div class="alert-box alertBoxAddSite">
	    <div class="tabularCustomHead">Add/Edit Site Information</div>
	    <div class="tabularContainer">
	    	<form name="addEditSiteForm" id="addEditSiteForm" action="">
				<h2 class='autoWidth'>Add/Edit Site Information</h2>
				<div class='hr'></div>
				<c:if test="${message ne null}">
					<div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close"  onclick="showMe('messagediv', this)"></div>
				</c:if>
			    	<div>&nbsp;</div>
				<div class="formcontainer">
					<div class="row">
						  <span class="label equalForms"><label><span class="required">*</span>Site Name:</label></span>
						  <span class="formfield equalForms"><input name="siteNameOverlay" maxlength="90" type="text" class="input" id="siteNameOverlay"/></span>
					</div>
					<div class="row">
						  <span class="label equalForms"><label><span class="required">*</span>Address 1:</label></span>
						  <span class="formfield equalForms"><input name="address1Overlay" maxlength="60" type="text" class='input' id="address1Overlay" /></span>
					</div>
					<div class="row">
						  <span class="label equalForms"><label>Address 2:</label></span>
						  <span class="formfield equalForms"><input name="address2Overlay" maxlength="60" type="text" class='input' id="address2Overlay" /></span>
					</div>
					<div class="row">
						  <span class="label equalForms"><label><span class="required">*</span>City:</label></span>
						  <span class="formfield equalForms"><input name="cityOverlay" maxlength="40" type="text" class='input' id="cityOverlay" /></span>
					</div>
					<div class="row">
						  <span class="label equalForms"><label><span class="required">*</span>State:</label></span>
						  <span class="formfield equalForms">
						  	<select name="stateOverlay" class='widthFull' id="stateOverlay">
						  		<option value=" "  selected="selected"> </option><option value="AK" >AK</option><option value="AL" >AL</option><option value="AR" >AR</option><option value="AS" >AS</option><option value="AZ" >AZ</option><option value="CA" >CA</option><option value="CO" >CO</option><option value="CT" >CT</option><option value="DC" >DC</option><option value="DE" >DE</option><option value="FL" >FL</option><option value="GA" >GA</option><option value="GU" >GU</option><option value="HI" >HI</option><option value="IA" >IA</option><option value="ID" >ID</option><option value="IL" >IL</option><option value="IN" >IN</option><option value="KS" >KS</option><option value="KY" >KY</option><option value="LA" >LA</option><option value="MA" >MA</option><option value="MD" >MD</option><option value="ME" >ME</option><option value="MI" >MI</option><option value="MN" >MN</option><option value="MO" >MO</option><option value="MP" >MP</option><option value="MS" >MS</option><option value="MT" >MT</option><option value="NC" >NC</option><option value="ND" >ND</option><option value="NE" >NE</option><option value="NH" >NH</option><option value="NJ" >NJ</option><option value="NM" >NM</option><option value="NV" >NV</option><option value="NY" >NY</option><option value="OH" >OH</option><option value="OK" >OK</option><option value="OR" >OR</option><option value="PA" >PA</option><option value="PR" >PR</option><option value="RI" >RI</option><option value="SC" >SC</option><option value="SD" >SD</option><option value="TN" >TN</option><option value="TX" >TX</option><option value="UT" >UT</option><option value="VA" >VA</option><option value="VI" >VI</option><option value="VT" >VT</option><option value="WA" >WA</option><option value="WI" >WI</option><option value="WV" >WV</option><option value="WY" >WY</option>
						  	</select>
						  </span>
					</div>
					<div class="row">
						  <span class="label equalForms"><label><span class="required">*</span>Zip Code:</label></span>
						  <span class="formfield equalForms"><input name="zipcodeOverlay" maxlength="5" type="text" class='input' validate="number" id="zipcodeOverlay" /></span>
					</div>
					<input type="hidden" id="addressRelatedData" />
					<input type="hidden" id="indexOpened" />
				</div>
			    <div class="buttonholder">
			    	<input type="button" class="graybtutton"  value="Cancel" id="cancelOverlay"/>
			    	<input type="submit" class="button"  value="Save" id="saveOverlay"/>
			    </div>
			</form>
	    </div>
	    <a href="javascript:void(0);" class="exit-panel exit-panel-add-site"></a> 
	</div>
	<%-- Overlay Pop up Ends --%>
	
	<%-- Pop up start for Address Validation --%>
	<div class="alert-box alert-box-address">
	   <div id="newTabs">
	   		<div class="tabularCustomHead">Address Validation</div>
	  		<div id="addressDiv" class='evenRows'></div>
	  </div>
	  <a href="javascript:void(0);" class="exit-panel address-exit-panel" >&nbsp;</a>
	</div>
	<%-- Pop up Ends for Address Validation --%>
	
	<%-- Pop up starts for Comments--%>
	<div class="alert-box alert-box-proposal-comments" id="overlayDivId">
	</div>
	<%-- Pop up ends for Comments--%>
 </nav:navigationSM>