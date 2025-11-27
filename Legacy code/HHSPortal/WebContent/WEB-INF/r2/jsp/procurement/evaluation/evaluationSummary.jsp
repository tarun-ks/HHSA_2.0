<%-- This jsp file has been added in R4--%>
<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects/>
<nav:navigationSM screenName="EvaluationSummary"> 
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/evaluationSummary.js"></script>
<portlet:actionURL var="evaluationSummaryFormURL" escapeXml="false">
	<portlet:param name="action" value="propEval" />
	<portlet:param name="topLevelFromRequest" value="ProposalsandEvaluations"/>
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}" />
	<portlet:param name="ES" value="0"/>
</portlet:actionURL>
<%-- Close Submissions  URL Starts --%>
<portlet:resourceURL var="closeSubmissions" id="closeSubmissions" escapeXml='false'>
	<portlet:param name="procurementId" value="${procurementBean.procurementId}" />
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}" />
	<portlet:param name="action" value="propEval" />
</portlet:resourceURL>
<%-- Close Submissions  URL Ends --%>
<%-- Close Group URL Starts --%>
<portlet:resourceURL var="closeGroup" id="closeGroup" escapeXml='false'>
	<portlet:param name="action" value="propEval" />
</portlet:resourceURL>
<%-- Close Group URL Ends --%>
<portlet:resourceURL var="cancelCompetition" id="cancelCompetition" escapeXml='false'>
	<portlet:param name="procurementId" value="${procurementBean.procurementId}" />
	<portlet:param name="action" value="propEval" />
</portlet:resourceURL>
<%-- Close All Submissions URL Starts --%>
<portlet:resourceURL var="closeAllSubmission" id="closeAllSubmission" escapeXml='false'>
	<portlet:param name="procurementId" value="${procurementBean.procurementId}" />
	<portlet:param name="evaluationGroupId" value="${evaluationGroupId}" />
	<portlet:param name="action" value="propEval" />
</portlet:resourceURL>
<%-- Close All Submissions URL Ends --%>
	<form id="evaluationSummaryForm" name="evaluationSummaryForm" action="${evaluationSummaryFormURL}" method ="post" commandName="EvaluationSummaryBean">
		<input type="hidden" id="procurementId" name="procurementId" value="${procurementBean.procurementId}"/>
		<input type = 'hidden' value='${closeSubmissions}' id='closeSubmissions'/>
		<input type = 'hidden' value='${closeGroup}' id='closeGroup'/>
		<input type = 'hidden' value='${cancelCompetition}' id='cancelCompetition'/>
		<input type = 'hidden' value='${closeAllSubmission}' id='closeAllSubmission'/>
		<%-- QC 9069  Disable Evaluation Settings tab for RFPs in selections made, closed and cancelled statuses for read only users --%>
		<input type="hidden" id="role_current" name = "role_current" value="<%= session.getAttribute("role_current") %>"/>
		
		<c:set var="now" value="<%=new java.util.Date()%>" />
		<c:if test="${procurementBean.isOpenEndedRFP eq '1' and fn:length(evaluationGroupList) > 1}">
			<select id="evalGroupDropDown" class="floatRht" >
				<option value="-1">Select Evaluation Group</option>
				<c:forEach var="entry" items="${evaluationGroupList}">
					<option value="${entry['EVALUATION_GROUP_ID']}">${entry['EVALUATION_GROUP_TITLE']}</option>
	            </c:forEach>
			</select>
		</c:if>
		<h2>Proposals and Evaluations Summary
		<c:if test="${procurementBean.isOpenEndedRFP eq '1'}">
			<a id="returnEvaluationGroups" class="floatRht returnButton" href="#">Evaluation Groups</a>
		</c:if>
		</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title='Need Help?' onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Proposals and Evaluations Summary" name="screenName"/>
		</d:content>	
			<div class='hr'></div>
			<c:if test="${message ne null}">
				<div id="messagediv" class="${messageType}" style="display: block;">
				${message}<c:if test="${closeSubmittion ne null and closeSubmittion eq 'closeSubmittionSuccess'}">  <fmt:formatDate pattern="MM/dd/yyyy @ hh:mm a" type="both"  value="${now}" /> &nbsp;EST.</c:if> <img onclick="showMe('messagediv', this)" 
					class="message-close" id="box"
					src="../framework/skins/hhsa/images/iconClose.jpg">
				</div>
			</c:if>
			<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
				<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record will be displayed in Read-only format.</div>
			</c:if>
			<c:choose>
				<c:when test="${procurementBean.isOpenEndedRFP eq '1'}">
					<div class="formcontainer">
						<div class="row">
							<span class="label">Evaluation Group:</span>
							<span class="formfield">${groupTitleMap['EVALUATION_GROUP_TITLE']}</span>
						</div>
						<div class="row">
							<span class="label">Closing Date:</span>
							<span class="formfield">${groupTitleMap['SUBMISSION_CLOSE_DATE']}</span>
						</div>
					</div>
					
					<%--Close Submission section starts for open ended RFP--%>
					<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) or (procurementBean.status eq '7' or procurementBean.status eq '8')}">
						<c:set var="sectionCloseSubmmittion"><%=HHSComponentMappingConstant.S215_CLOSE_SUBMITTION_BUTTON%></c:set>
						<d:content section="${sectionCloseSubmmittion}">
							<c:if test="${loCloseButtonVisibleStatus eq 'Y'}">
								<div class="taskButtons" style="float:none;">
									<span class="floatLft">
										<input type="button" value="Close Group: Allow Submissions" class='cancelGreen' onclick="closeGroupSubmissions();"/>
									</span>
								</div>
							</c:if>
						</d:content>
						
						<c:set var="sectionAllCloseSubmmittion"><%=HHSComponentMappingConstant.S215_CLOSE_ALL_SUBMITTION_BUTTON%></c:set>
						<d:content section="${sectionAllCloseSubmmittion}">
							<c:if test="${loCloseButtonVisibleStatus eq 'Y'}">
								<div class="taskButtons" style="float:none;">
										<span class="floatRgt">
											<input type="button" value="Close ALL Submissions" class='cancelRed floatRht' onclick="closeAllSubmissions();"/>
										</span>
								</div>
							</c:if>
						</d:content>
					</d:content>
					
				</c:when>
				<c:otherwise>
					<%--Close Submission section starts for non open ended RFP--%>
					<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) or (procurementBean.status eq '7' or procurementBean.status eq '8')}">
						<c:set var="sectionCloseSubmmittion"><%=HHSComponentMappingConstant.S215_CLOSE_SUBMITTION_BUTTON%></c:set>
						<d:content section="${sectionCloseSubmmittion}">
							<c:if test="${loCloseButtonVisibleStatus eq 'Y'}">
								<div class="taskButtons">
									<span class="floatLft">
										<input type="button" value="Close Submissions" class='btnCancel' onclick="closeProposalSubmissions();" <c:if test="${state eq 'disable'}">disabled="disabled"</c:if>/>
									</span>
								</div>
							</c:if>
						</d:content>
					</d:content>
				</c:otherwise>
			</c:choose>
				<%--Close Submission section ends --%>
				<%-- Grid Starts --%>
				<%-- Container Starts --%>
			   <div class="tabularWrapper gridfixedHeight">
			        <st:table objectName="evaluationSummaryList" displayTitle="no" cssClass="heading" 
						alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
						<st:property headingName="Competition Pool" columnName="competitionPoolTitle" align="left" size="25%" sortType="competitionPoolTitle" sortValue="asc">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationSummaryExtension" />
						</st:property>
						<st:property headingName="Providers Submitted" columnName="providersSubmitted" align="left" size="10%" sortType="providersSubmitted" sortValue="asc"/>
						<st:property headingName="Proposals Submitted" columnName="proposalsSubmitted" align="left" size="10%" sortType="proposalsSubmitted" sortValue="asc"/>
						<st:property headingName="Competition Pool Status" columnName="evaluationStatus" align="left" size="15%" sortType="evaluationStatus" sortValue="asc"/>
						<st:property headingName="Evaluations In Progress" columnName="evaluationsInProgress" align="left" size="10%" sortType="evaluationsInProgress" sortValue="asc"/>
						<st:property headingName="Evaluations Complete" columnName="evaluationsComplete" align="left" size="10%" sortType="evaluationsComplete" sortValue="asc"/>
						<st:property headingName="Actions" columnName="actions" align="left" size="20%">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationSummaryExtension" />
						</st:property>
					</st:table>
				</div>
			    <%-- Grid Ends --%>
		</form>
	</nav:navigationSM>
	<%-- Overlay Starts --%>
	<div class="overlay"></div>
	<div class="alert-box alert-box-closeEvaluationTasks">
		<div class='tabularCustomHead'>Close Submissions</div>
		<a href="javascript:void(0);" class="exit-panel close-submittion">&nbsp;</a>
		<div id="submissionClose"></div>
	</div>
	<div class="alert-box alert-box-closeGroupSubmission">
		<div class='tabularCustomHead'>Close Group: Allow Submissions</div>
		<a href="javascript:void(0);" class="exit-panel close-group-submittion">&nbsp;</a>
		<div id="groupSubmissionClose"></div>
	</div>
	<div class="alert-box alert-box-cancelCompetition">
		<div class='tabularCustomHead'>Confirm Competition Cancellation</div>
		<a href="javascript:void(0);" class="exit-panel cancel-competition">&nbsp;</a>
		<div id="cancelCompetitionOverlay"></div>
	</div>
	<div class="alert-box alert-box-closeAllSubmissions">
		<div class='tabularCustomHead'>Close ALL Submissions</div>
		<a href="javascript:void(0);" class="exit-panel close-all-submittion">&nbsp;</a>
		<div id="allSubmissionClose"></div>
	</div>
	