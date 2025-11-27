<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/procurementlist.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>

<portlet:actionURL var="addProcurementUrl" escapeXml="false">
</portlet:actionURL>
<portlet:resourceURL var="getProgramListForAgency" id="getProgramListForAgency"></portlet:resourceURL>
<portlet:resourceURL var='procurementEpinList' id='procurementEpinList' escapeXml='false'></portlet:resourceURL>
<%-- Code updated for R4 Starts --%>
<portlet:resourceURL var='saveFavorites' id='saveFavorites' escapeXml='false'></portlet:resourceURL>
<%-- Code updated for R4 Ends --%>
<input type="hidden" id="procurementEpinList" value="${procurementEpinList}"/>
<input type="hidden" id="getProgramListForAgency" value="${getProgramListForAgency}"/>
<%-- Code updated for R4 Starts --%>
<input type="hidden" id="saveFavorites" value="${saveFavorites}"/>
<%-- Code updated for R4 Ends --%>	
	<form:form name="procform" id="procform" action="${addProcurementUrl}" method ="post" commandName="Procurement">
		<input type="hidden" id="submit_action" name="submit_action" value=""/>
		<input type="hidden" id="topLevelFromRequest" name="topLevelFromRequest" value=""/>
		<input type="hidden" id="midLevelFromRequest" name="midLevelFromRequest" value=""/>
		<h2>Procurement Roadmap</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title='Need Help?' onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Procurement Roadmap" name="screenName"/>
		</d:content>	
			<div class='hr'></div>
			<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" onclick="showMe('messagediv', this)">
				</div>
			</c:if>
	<%-- Container Starts --%>
				<c:set var="sectionProviderSection"><%=HHSComponentMappingConstant.S201_PROVIDER_SECTION%></c:set>
				<d:content section="${sectionProviderSection}">
                    <p>
					<font style="color:red"><b>IMPORTANT NOTICE</b></font>: HHS RFPs are now released through the <a class="link" title="PASSPort Public Portal" target="_blank" href="https://passport.cityofnewyork.us/page.aspx/en/rfp/request_browse_public"><b>PASSPort Public Portal</b></a>. To submit a proposal in PASSPort, you must have a PASSPort account. Click <a class="link" title="PASSPort Login" target="_blank" href="https://passport.cityofnewyork.us/page.aspx/en/usr/login?blockSSORedirect=false&%20ReturnUrl=/page.aspx/en/buy/homepage">here</a> to create an account using your NYC.ID and/or to log into an existing PASSPort account. The <b>same</b> NYC.ID login credentials you use to access HHS Accelerator may be used to login or create an account in PASSPort.
					</p>

					<p>
					The Procurement Roadmap displays all NYC Client and Community Services Procurements that were previously released in HHS Accelerator and allows you to filter on details such as your status, Agency, and RFP release dates. You may also sort the list by each column.
					</p>
					<p>
						Saved Provider Favorites are shared across your organization. You can update your organization's favorite HHS Accelerator Procurements by checking and unchecking Provider Favorites checkboxes and clicking the 'Save Updates to Provider Favorites' button.
					</p>
				</d:content>
				<d:content section="${sectionAcceleratorAgencySection}">
				</d:content>
				<%--Filter and Reassign section starts --%>
				<div class="taskButtons nowrap">
						<span class='floatLft'><input type="button" value="Filter Items"  
								id= "filterbutton" class="filterDocument marginReset" onclick="setVisibility('documentValuePop', 'inline');"/></span>
				<c:set var="draftProcurement"><fmt:message key='PROCUREMENT_DRAFT'/></c:set>
				<c:set var="plannedProcurement"><fmt:message key='PROCUREMENT_PLANNED'/></c:set>
				<c:set var="releasedProcurement"><fmt:message key='PROCUREMENT_RELEASED'/></c:set>
				<c:set var="proposalReceived"><fmt:message key='PROCUREMENT_PROPOSALS_RECEIVED'/></c:set>
				<c:set var="evaluationComplete"><fmt:message key='PROCUREMENT_EVALUATIONS_COMPLETE'/></c:set>
				<c:set var="selectionMade"><fmt:message key='PROCUREMENT_SELECTIONS_MADE'/></c:set>
				<c:set var="cancelled"><fmt:message key='PROCUREMENT_CANCELLED'/></c:set>
				<c:set var="closed"><fmt:message key='PROCUREMENT_CLOSED'/></c:set>
				<c:set var="provEligibleToPropose"><fmt:message key='PROVIDER_ELIGIBLE_TO_PROPOSE'/></c:set>
				<c:set var="provServiceAppRequired"><fmt:message key='PROVIDER_SERVICE_APP_REQUIRED'/></c:set>
				<c:set var="provDraft"><fmt:message key='PROVIDER_DRAFT'/></c:set>
				<c:set var="provSubmittedProposal"><fmt:message key='PROVIDER_SUBMITTED_PROPOSAL'/></c:set>
				<c:set var="provNotSelected"><fmt:message key='PROVIDER_NOT_SELECTED'/></c:set>
				<c:set var="provSelected"><fmt:message key='PROVIDER_SELECTED'/></c:set>
				<c:set var="provDidNotPropose"><fmt:message key='PROVIDER_DID_NOT_PROPOSE'/></c:set>
				<c:set var="provNotApplicable"><fmt:message key='PROVIDER_NOT_APPLICABLE'/></c:set>
				<%-- Code updated for R4 Starts --%>
				<c:set var="provNonResponsive"><fmt:message key='PROVIDER_NON_RESPONSIVE'/></c:set>	
				<%-- Start Release 5 user notification --%>
					<d:content section="${sectionProviderSection}" isReadOnly="false">
						<span class='floatLft'>
							<div class="buttonholder">
							<c:if test="${procurementRoadMapReadonlyFlag ne true}">
							&nbsp;<input type="button" value="Save Updates to Provider Favorites" id="saveUpdatesToFavorite"/>
							</c:if>
								<c:choose>
									<c:when test="${Procurement.isFavoriteDisplayed ne 'true'}">
										&nbsp;<input type="button" value="Display Provider Favorites Only" class="graybtutton" id="displayFavoritesOnly"/>
									</c:when>
									<c:otherwise>
										&nbsp;<input type="button" value="Display All Procurements" class="graybtutton" id="displayAll"/>
									</c:otherwise>
								</c:choose>
							</div>
						</span>
					</d:content>
					<%-- Start Release 5 user notification --%>
					<%-- Code updated for R4 Ends --%>
					<c:set var="sectionAcceleratorAgencySection"><%=HHSComponentMappingConstant.S201_ACCELERATOR_AGENCY_SECTION%></c:set>
						<d:content section="${sectionAcceleratorAgencySection}">
						
				<!-- Begin QC8914 R7.2.0 Oversight Role Hide 'Add Procurement' Button -->
				<% 
				 if(! CommonUtil.hideForOversightRole(request.getSession()))
				{%>
						<c:set var="sectionAddProcurement"><%=HHSComponentMappingConstant.S201_ADD_PROCUREMENT_BUTTON%></c:set>
						<d:content section="${sectionAddProcurement}">
							<span class='floatLft'>&nbsp;<input type="button" value="Add Procurement" class="add" onclick="addProcurement();"/></span>
						</d:content>
				<%}%>		
				<!-- End QC8914 R7.2.0 Oversight Role Hide 'Add Procurement' Button -->		
				
						<c:set var="sectionProcurementCount"><%=HHSComponentMappingConstant.S201_PROCUREMENT_COUNT%></c:set>
						<d:content section="${sectionProcurementCount}">
							<span class='count'><span style="padding-top:13px;">Procurements: <label><c:out value="${totalCount}"/></label></span></span> 
						</d:content>
						<%-- Popup for Filter Task Accelerator Starts --%>
						<div id="documentValuePop" class='formcontainerFinance dateValidateWrapper' style='width:460px;'>
						<div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');">X</a></div>
						<div class='row'>
							<span class='label'>Procurement Title:</span>
							<span class='formfield'>
								<form:input path="procurementTitle" cssClass="proposalConfigDrpdwn" id="procurementTitle" maxlength="120" onchange="enableDisableDefaultFilter()" />
							</span>
							<span class="error"></span>
						</div>
						<c:set var="sectionProcurementEPIN"><%=HHSComponentMappingConstant.S201_PROCUREMENT_EPIN%></c:set>
						<d:content section="${sectionProcurementEPIN}">
							<div class='row'>
								<span class='label'>Procurement <br/>E-PIN:</span>
								<span class='formfield'>
								<%--R6: updated maxlength for new epin format --%>
									<form:input path="procurementEpin" cssClass="proposalConfigDrpdwn" id="procurementEpin" maxlength="20" validate="alphaNumericEpin"/>
								</span>
								<span class="error" id="epinError"></span>
							</div>
						</d:content>
						<div class='row' id="agencyDiv">
							<span class='label'>Agency:</span>
							<span class='formfield'>
								<form:select path="agencyId"  class='widthFull' id="agency">
									<form:option id="All NYC Agencies" value="All NYC Agencies"></form:option>
									<c:forEach var="agencyNameVar" items="${agencySet}">
										<c:choose>
											<c:when test="${selectedAgency eq agencyNameVar.key}">
												<option selected="selected" value="${agencyNameVar.key}" title="${agencyNameVar.value}">${agencyNameVar.value}</option>
											</c:when>
											<c:otherwise>
								                <option value="${agencyNameVar.key}" title="${agencyNameVar.value}">${agencyNameVar.value}</option>
								           	</c:otherwise>
										</c:choose>
									</c:forEach>
								</form:select>
							</span>
						</div>
						<div class='row' id="programNameDiv">
							<div>
								<span class='label'>Program Name:</span>
								<span class='formfield'>
									<form:select path="programName"  class='widthFull' id="programName" onchange="enableDisableDefaultFilter()">
										<form:option id="-1" value=""></form:option>
										<c:forEach var="programNameVar" items="${programNameList}">
											<c:choose>
												<c:when test="${selectedProgramName eq programNameVar.programId}">
													<option selected="selected" value="${programNameVar.programId}" title="${programNameVar.programName}">${programNameVar.programName}</option>
												</c:when>
												<c:otherwise>
									                <option value="${programNameVar.programId}" title="${programNameVar.programName}">${programNameVar.programName}</option>
									           	</c:otherwise>
											</c:choose>
										</c:forEach>
									</form:select>
								</span>
							</div>
						</div>
						<div class='row' id="ActiveProcurementDiv">
						<c:set var="role_current"><%= session.getAttribute("role_current") %></c:set>
							<span class='label'>Active Procurement Statuses:</span>
	            			<span class='formfield' id='firstLevelCheckBox'>
								<span class='leftColumn floatRht'>
									<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${proposalReceived}" id='chkProposal' /><label for='chkProposal'>Proposals Received</label></span>
						 			<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${evaluationComplete}" id='chkEvaluations'  /><label for='chkEvaluations'>Evaluations Complete</label></span>
						  			<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${selectionMade}" id='chkSelection'  /><label for='chkSelection'>Selections Made</label></span>
								</span>
								<span class='rightColumn floatLft'>
								    <!-- QC 8914 -->
								    <% 
				 					if(! CommonUtil.hideForOversightRole(request.getSession()))
									{%>
										<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${draftProcurement}" id='chkDraft' /><label for='chkDraft'>Draft</label></span>
						 			<%}%>
						 			<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${plannedProcurement}" id='chkPlanned'  /><label for='chkPlanned'>Planned</label></span>
						 			<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${releasedProcurement}" id='chkReleased'  /><label for='chkReleased'>Released</label></span>
						 		</span>
								
	            			</span>
						</div>
						<div class='row' id="InactiveProcurementDiv">
							<span class='label'>Inactive Procurement Statuses:</span>
	                     	<span class='formfield' id='secondLevelCheckBox'>
	              				<span class='leftColumn floatRht'>
						 			<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" value="${cancelled}" cssClass="noSelectSetting" id='chkCancelled' /><label for='chkCancelled'>Cancelled</label></span>
								</span>
								<span class='rightColumn floatLft'>
									<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" value="${closed}" cssClass="noSelectSetting" id='chkClosed' /><label for='chkClosed'>Closed</label></span>
								</span>
	           				</span>
						</div>
						<div class='row' id="serviceDiv">
							<span class='label'>Service:</span>
							<span class='formfield'>
								<form:select id="service" path="serviceName" cssClass="proposalConfigDrpdwn" items="${serviceMap}" onchange="enableDisableDefaultFilter()">
								</form:select>
							</span>
						</div>
						<div class='row'>
							<span class='label'>Release Date from:</span>
							<span class='formfield'>
								<span class='floatLft'>							
									<form:input startEnd="true" cssClass='datepicker' path="releaseFrom" id="releasedatefrom" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
									<img src="../framework/skins/hhsa/images/calender.png"  onclick="NewCssCal('releasedatefrom',event,'mmddyyyy');return false;"/> 
									&nbsp;&nbsp;
								</span>
								<span class="error clear"></span>
								to:
								<span>							
										<form:input startEnd="true" cssClass='datepicker' path="releaseTo" id="releasedateto" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
										<img src="../framework/skins/hhsa/images/calender.png"  onclick="NewCssCal('releasedateto',event,'mmddyyyy');return false;"/>
								</span>
								<span class="error clear"></span>
							</span>
						</div>
						<div class='row'>
						<%-- Code updated for R4 Starts --%>
							<span class='label'>Open Ended:</span>
							<span class='formfield'>
								<span class='leftColumn'>
									<span><form:checkbox path="isOpenEndedRFP" onchange="enableDisableDefaultFilter()" id="isOpenEndedRFP" value="1"/><label for='isOpenEndedRFP'>Show only open-ended</label></span>
								</span>
							</span>
						</div>
						<div class='row'>
							<%-- Code updated for R4 Ends --%>
							<span class='label'>Proposal Due Date from:</span>
							<span class='formfield'>	
								<span class='floatLft'>						
									<form:input startEnd="true" cssClass='datepicker' path="proposalDueFrom" id="proposalduedatefrom" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
									<img src="../framework/skins/hhsa/images/calender.png"  onclick="NewCssCalLocal('proposalduedatefrom',event,'mmddyyyy');return false;"/> 
									&nbsp;&nbsp;
								</span>
							<span class="error clear"></span>
								to:
								<span>							
									<form:input startEnd="true" cssClass='datepicker' path="proposalDueTo" id="proposalduedateto" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
									<img src="../framework/skins/hhsa/images/calender.png"  onclick="NewCssCalLocal('proposalduedateto',event,'mmddyyyy');return false;"/>
								</span>
								<span class="error clear"></span>
							</span>
						</div>
						<div class='row'>
							<span class='label'>Contract Start Date from:</span>
							<span class='formfield'>
								<span class='floatLft'>							
									<form:input startEnd="true" cssClass='datepicker' path="contractStartFrom" id="contractstartdatefrom" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
									<img src="../framework/skins/hhsa/images/calender.png"  onclick="NewCssCalLocal('contractstartdatefrom',event,'mmddyyyy');return false;"/> 
									&nbsp;&nbsp;
								</span>
								<span class="error clear" ></span>
								to:
								<span>							
									<form:input startEnd="true" cssClass='datepicker' path="contractStartTo" id="contractstartdateto" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
									<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCalLocal('contractstartdateto',event,'mmddyyyy');return false;"/>
								</span>
								<span class="error clear"></span>
							</span>
						</div>
						<div class="buttonholder">
							<input type="button" id="filterClear" value="Clear Filters" onclick="filterCleaning()" class="graybtutton"/>
							<input type="button" id="clearfilter" value="Set to Default Filters" onclick="clearProcurementFilter()" class="graybtutton"/>
							<input type="button" value="Filter" id="filter" onclick="displayFilter()"/>
						</div> 
					</div>
						<%-- Popup for Filter Task Accelerator Ends --%>
					</d:content>
					<c:set var="sectionProviderSection"><%=HHSComponentMappingConstant.S201_PROVIDER_SECTION%></c:set>
						<d:content section="${sectionProviderSection}" isReadOnly="false">
						<%-- Popup for Filter Task Provider Starts --%>
						<span class='count'><span style="padding-top:13px;">Procurements: <label><c:out value="${totalCount}"/></label></span></span> 
						<div id="documentValuePop" class='formcontainerFinance' style='width:476px !important;'>
							<div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');">X</a></div>
							<c:set var="sectionProviderStatus"><%=HHSComponentMappingConstant.S201_PROVIDER_STATUS%></c:set>
							<d:content section="${sectionProviderStatus}" isReadOnly="false">
								<div class='row' id="ProviderStatusDiv">
									<span class='label'>Provider Statuses:</span>
			            			<span class='formfield' id='thirdLevelCheckBox'>
										<span class='leftColumn'>
											<span><form:checkbox path="providerStatusList" onchange="enableDisableDefaultFilter()" cssClass="providerStatusClass" id='providerStatus_eligibleToPropose' value="${provEligibleToPropose}"/><label for='providerStatus_eligibleToPropose'>Eligible to Propose</label></span>
								 			<span><form:checkbox path="providerStatusList" onchange="enableDisableDefaultFilter()" cssClass="providerStatusClass" id='providerStatus_serviceAppRequired' value="${provServiceAppRequired}"/><label for='providerStatus_serviceAppRequired'>Service App Required</label></span>
								 			<span><form:checkbox path="providerStatusList" onchange="enableDisableDefaultFilter()" cssClass="providerStatusClass" id='providerStatus_draft' value="${provDraft}"/><label for='providerStatus_draft'>Draft</label></span>
								 			<span><form:checkbox path="providerStatusList" onchange="enableDisableDefaultFilter()" cssClass="providerStatusClass" id='providerStatus_submittedProposal' value="${provSubmittedProposal}"/><label for='providerStatus_submittedProposal'>Submitted Proposal</label></span>
								 			<%-- Code updated for R4 Starts --%>
								 			<span><form:checkbox path="providerStatusList" onchange="enableDisableDefaultFilter()" cssClass="providerStatusClass" id='providerStatus_didNotPropose' value="${provDidNotPropose}"/><label for='providerStatus_didNotPropose'>Did Not Propose</label></span>
								 			<%-- Code updated for R4 Ends --%>
								 		</span>
										<span class='rightColumn'>
								 			<span><form:checkbox path="providerStatusList" onchange="enableDisableDefaultFilter()" cssClass="providerStatusClass" id='providerStatus_selected' value="${provSelected}"/><label for='providerStatus_selected'>Selected</label></span>
								  			<span><form:checkbox path="providerStatusList" onchange="enableDisableDefaultFilter()" cssClass="providerStatusClass" id='providerStatus_notSelected' value="${provNotSelected}"/><label for='providerStatus_notSelected'>Not Selected</label></span>
								  			<span><form:checkbox path="providerStatusList" onchange="enableDisableDefaultFilter()" cssClass="providerStatusClass" id='providerStatus_notApplicable' value="${provNotApplicable}"/><label for='providerStatus_notApplicable'>Not Applicable</label></span>
								  			<%-- Code updated for R4 Starts --%>
											<span><form:checkbox path="providerStatusList" onchange="enableDisableDefaultFilter()" cssClass="providerStatusClass" id='providerStatus_nonResponsive' value="${provNonResponsive}"/><label for='providerStatus_nonResponsive'>Non-Responsive</label></span>
										<%-- Code updated for R4 Ends --%>
										</span>
			            			</span>
								</div>
							</d:content>
							<div class='row' id="ActiveProcurementDiv">
								<span class='label'>Active Procurement Statuses:</span>
		            			<span class='formfield' id='firstLevelCheckBox'>
									<span class='leftColumn'>
							 			<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${plannedProcurement}" id='chkPlanned'  /><label for='chkPlanned'>Planned</label></span>
							 			<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${releasedProcurement}" id='chkReleased'  /><label for='chkReleased'>Released</label></span>
							 		</span>
									<span class='rightColumn'>
										<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${proposalReceived}" id='chkProposal' /><label for='chkProposal'>Proposals Received</label></span>
							  			<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" cssClass="selectedSetting" value="${selectionMade}" id='chkSelection'  /><label for='chkSelection'>Selections Made</label></span>
									</span>
		            			</span>
							</div>
							<div class='row' id="InactiveProcurementDiv">
								<span class='label'>Inactive Procurement Statuses:</span>
		                     	<span class='formfield' id='secondLevelCheckBox'>
		              				<span class='leftColumn'>
							 			<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" value="${closed}" cssClass="noSelectSetting" id='chkClosed' /><label for='chkClosed'>Closed</label></span>
									</span>
									<span class='rightColumn'>
										<span><form:checkbox path="procurementStatusList" onchange="enableDisableDefaultFilter()" value="${cancelled}" cssClass="noSelectSetting" id='chkCancelled' /><label for='chkCancelled'>Cancelled</label></span>
									</span>
		           				</span>
							</div>
							<div class='row'>
								<span class='label'>Procurement Title:</span>
								<span class='formfield'>
									<form:input path="procurementTitle" cssClass="proposalConfigDrpdwn" id="procurementTitle" maxlength="120" onchange="enableDisableDefaultFilter()" />
								</span>
								<span class="error"></span>
							</div>
							<div class='row' id="agencyDiv">
								<span class='label'>Agency:</span>
								<span class='formfield'>
									<form:select path="agencyId" class='widthFull' id="agency">
										<form:option id="All NYC Agencies" class='widthFull' value="All NYC Agencies"></form:option>
										<c:forEach var="agencyNameVar" items="${agencySet}">
											<c:choose>
												<c:when test="${selectedAgency eq agencyNameVar.key}">
													<option selected="selected" value="${agencyNameVar.key}" title="${agencyNameVar.value}">${agencyNameVar.value}</option>
												</c:when>
												<c:otherwise>
									                <option value="${agencyNameVar.key}" title="${agencyNameVar.value}">${agencyNameVar.value}</option>
									           	</c:otherwise>
											</c:choose>
										</c:forEach>
									</form:select>
								</span>
							</div>
							<div class='row' id="programNameDiv">
								<span class='label'>Program Name:</span>
								<span class='formfield'>
									<form:select path="programName" class='widthFull' id="programName" onchange="enableDisableDefaultFilter()">
										<form:option id="-1" value=""></form:option>
										<c:forEach var="programNameVar" items="${programNameList}">
											<c:choose>
												<c:when test="${selectedProgramName eq programNameVar.programId}">
													<option selected="selected" value="${programNameVar.programId}" title="${programNameVar.programName}">${programNameVar.programName}</option>
												</c:when>
												<c:otherwise>
									                <option value="${programNameVar.programId}" title="${programNameVar.programName}">${programNameVar.programName}</option>
									           	</c:otherwise>
											</c:choose>
										</c:forEach>
									</form:select>
								</span>
							</div>
							<div class='row' id="serviceDiv">
								<span class='label'>Service:</span>
								<span class='formfield'>
									<form:select id="service" path="serviceName" cssClass="widthFull" items="${serviceMap}" onchange="enableDisableDefaultFilter()">
									</form:select>
								</span>
							</div>
							<div class='row'>
								<span class='label'>Release Date from:</span>
								<span class='formfield'>
								<span class='floatLft'>								
										<form:input startEnd="true" cssClass='datepicker' path="releaseFrom" id="releasedatefrom" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
										<img src="../framework/skins/hhsa/images/calender.png"  onclick="NewCssCal('releasedatefrom',event,'mmddyyyy');return false;"/> 
										&nbsp;&nbsp;
								</span>
								<span class="error clear"></span>
								to:
								<span>							
										<form:input startEnd="true" cssClass='datepicker' path="releaseTo" id="releasedateto" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
										<img src="../framework/skins/hhsa/images/calender.png"  onclick="NewCssCal('releasedateto',event,'mmddyyyy');return false;"/>
								</span>
								<span class="error clear"></span>
								</span>
							</div>
							<div class='row'>
							<%-- Code updated for R4 Starts --%>
								<span class='label'>Open-Ended:</span>
								<span class='formfield'>
									<span class='leftColumn'>
										<span><form:checkbox path="isOpenEndedRFP" onchange="enableDisableDefaultFilter()" id="isOpenEndedRFP" value="1"/><label for='isOpenEndedRFP'>Show only open-ended</label></span>
									</span>
								</span>
							</div>
							<div class='row'>
								<%-- Code updated for R4 Ends --%>
								<span class='label'>Proposal Due Date from:</span>
								<span class='formfield'>	
								<span class='floatLft'>							
										<form:input startEnd="true" cssClass='datepicker' path="proposalDueFrom" id="proposalduedatefrom" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
										<img src="../framework/skins/hhsa/images/calender.png"  onclick="NewCssCalLocal('proposalduedatefrom',event,'mmddyyyy');return false;"/> 
										&nbsp;&nbsp;
								</span>
								<span class="error clear"></span>
								to:
								<span>							
										<form:input startEnd="true" cssClass='datepicker' path="proposalDueTo" id="proposalduedateto" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
										<img src="../framework/skins/hhsa/images/calender.png"  onclick="NewCssCalLocal('proposalduedateto',event,'mmddyyyy');return false;"/>
								</span>
								<span class="error clear"></span>
								</span>
							</div>
							<div class='row'>
							<span class='label'>Contract Start Date from:</span>
							<span class='formfield'>
							<span class='floatLft'>								
									<form:input startEnd="true" cssClass='datepicker' path="contractStartFrom" id="contractstartdatefrom" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
									<img src="../framework/skins/hhsa/images/calender.png"  onclick="NewCssCalLocal('contractstartdatefrom',event,'mmddyyyy');return false;"/> 
									&nbsp;&nbsp;
							</span>
							<span class="error clear"></span>
							to:
							<span>							
									<form:input startEnd="true" cssClass='datepicker' path="contractStartTo" id="contractstartdateto" validate="calender" maxlength="10" onchange="enableDisableDefaultFilter()"/>
									<img src="../framework/skins/hhsa/images/calender.png"  onclick="NewCssCalLocal('contractstartdateto',event,'mmddyyyy');return false;"/>
							</span>
							<span class="error clear"></span>
							</span>
						</div>
						<div class="buttonholder">
 							<input type="button" id="filterClear" value="Clear Filters" onclick="filterCleaning()" class="graybtutton"/> 						
							<input type="button" id="clearfilter" value="Set to Default Filters" onclick="clearProcurementFilter()" class="graybtutton"/>
							<input type="button" value="Filter" id="filter" onclick="displayFilter()"/>
						</div> 
							
					</div>
					<%-- Popup for Filter Task Provider Ends --%>
				</d:content>
			</div>
			<%--Filter and Reassign section ends --%>
		
			<%-- Form Data Starts --%>
				<%-- Grid Starts --%>
			    <div  class="tabularWrapper procurementList clear" style='min-height:805px !important'>
			        <st:table objectName="procurementList"  displayTitle="no" cssClass="heading" 
						alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
						<%-- Code updated for R4 Starts --%>
						<jsp:scriptlet>
						if(CommonUtil.getConditionalRoleDisplay(HHSComponentMappingConstant.S201_PROVIDER_STATUS, request.getSession())) {
						</jsp:scriptlet>
							<st:property headingName="Provider Favorites" columnName="procurementFavorite" align="center" sortType="procurementFavorite" sortValue="asc"
								size="5%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProcurementTitleExtension" />
							</st:property>
						 <jsp:scriptlet>
						 }
						 </jsp:scriptlet>
						<%-- Code updated for R4 Ends --%>
						<st:property headingName="Procurement Title" columnName="procurementTitle" align="center" sortType="procurementTitle" sortValue="asc"
							size="31%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProcurementTitleExtension" />
						</st:property>
						 <jsp:scriptlet>
						 if(CommonUtil.getConditionalRoleDisplay(HHSComponentMappingConstant.S201_PROCUREMENT_EPIN, request.getSession())) {
						 </jsp:scriptlet>
						
							<st:property headingName="Procurement E&#8209;PIN" columnName="procurementEpin" sortType="procurementEpin" sortValue="asc"
								align="right" size="11%">
								<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ProcurementTitleExtension" />
							</st:property>
						 <jsp:scriptlet>
						 }
						 </jsp:scriptlet>
						<st:property headingName="Agency" columnName="agencyId" sortType="agencyId" sortValue="asc"
							align="left" size="10%" />
						<st:property headingName="Procurement Status" columnName="procurementStatus" sortType="procurementStatus" sortValue="asc"
							align="left" size="12%" />
						<jsp:scriptlet>
						if(CommonUtil.getConditionalRoleDisplay(HHSComponentMappingConstant.S201_PROVIDER_STATUS, request.getSession())) {
						</jsp:scriptlet>
							<st:property headingName="Provider Status" columnName="providerStatus" sortType="providerStatus" sortValue="asc"
								align="right" size="15%" />
						 <jsp:scriptlet>
						 }
						 </jsp:scriptlet>
						<st:property headingName="Release Date" columnName="releaseDate" sortType="releaseDate" sortValue="asc"
							 align="right" size="12%" />
						<st:property headingName="Proposal Due Date" columnName="proposalDueDate" sortType="proposalDueDate" sortValue="asc"
						 align="right" size="12%" />
						 <st:property headingName="Contract Date" columnName="contractStartDate" sortType="contractStartDate" sortValue="asc"
						 align="right" size="12%" />
					</st:table>
					<c:if test="${procurementList eq null or fn:length(procurementList) eq 0}">
						<div class="messagedivNycMsg noRecord" id="messagedivNycMsg">No Procurement found.</div>
					</c:if>
				
					<span class='count'><span>Procurements: <label><c:out value="${totalCount}"/></label></span></span> 
					
				</div>
				
			    <%-- Grid Ends --%>
			<form:hidden path="firstSort"/>
			<form:hidden path="secondSort"/>
			<form:hidden path="firstSortType"/>
			<form:hidden path="secondSortType"/>
			<form:hidden path="sortColumnName"/>
			<form:hidden path="firstSortDate"/>
			<form:hidden path="secondSortDate"/>
			<%-- Code updated for R4 Starts --%>
			<form:hidden path="isFavoriteDisplayed"/>
			<input type="hidden" name="favoriteIds" value="" id="favoriteIds"/>
			<input type="hidden" name="nonFavoriteIds" value="" id="nonFavoriteIds"/>
			<%-- Code updated for R4 Ends --%>
			<%-- Form Data Ends --%>
			
			<%-- Overlay Starts --%>
			<div class="overlay"></div>
			<div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>
			<div class="alert-box-contact">
				<div id="contactDiv"></div>
			</div>
			<input type="hidden" id="filterItem" name="filterItem" value="${filtered}"/>
		</form:form>
		<div id="overlayedJSPContent" style="display:none"></div>
		<input type="hidden" id="selectedAgency" value="${selectedAgency}"/>
		<input type="hidden" id="defaultAgency" value="${Procurement.organizationId}"/>
		<input type="hidden" id="orgType" value="${org_type}"/>