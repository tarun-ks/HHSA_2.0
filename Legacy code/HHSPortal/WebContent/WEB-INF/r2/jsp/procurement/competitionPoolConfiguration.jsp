<%--This jsp is used for competition pool configuration screen--%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="comSM" uri="/WEB-INF/tld/commonSolicitation.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/competitionPoolConfiguration.js"></script>
<style>
h2{width:82%}
.individualErrorMsg, .error{
	color:#D63301;
}
.formcontainer .row span.error {
    color: #D63301;
    float: left;
    padding: 4px 0;
    text-align: left;
    
    padding:0;
	background:transparent;
	line-height:auto;
	 *font-size: 0px;
}
.formcontainer .row span.error label.error{
	 *font-size:12px;
}

</style>

<%--defining action url on click of save button from the page --%>
<portlet:actionURL var="competitionPoolFormURL" escapeXml="false">
	<portlet:param name="submit_action" value="saveCompetitionPool"/>
	<portlet:param name="action" value="rfpRelease"/>
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
	<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}"/>
</portlet:actionURL>

<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<%--Navigation added on screen --%>
<nav:navigationSM screenName="CompetitionConfiguration">
<form id="competitionPoolForm" name="competitionPoolForm" action="${competitionPoolFormURL}" method ="post">
	<input type="hidden" value="${procurementBean.status}" id="procurementStatus"/>
	<div class='clear'></div>
	<%-- Form Data Starts --%>
	<div id='tabs-container'>
		<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
			<h2>Competition Configuration
				<%-- check for organization type --%>
				<input type='hidden' id='org_type' value='${org_type}' />
			</h2>
			<c:set var="readOnlyValue" value=""></c:set>
			<c:if test="${(org_type ne 'city_org') or ((procurementBean.status ne 2) and ( procurementBean.procurementAddendumFlag eq 0 ) )}">
				<c:set var="readOnlyValue" value="disabled='disabled'"></c:set>
			</c:if>
			<c:set var="sectionUnpublishInformation"><%=HHSComponentMappingConstant.S280_UNPUBLISHED_INFORMATION%></c:set>
			<d:content section="${sectionUnpublishInformation}">
				<c:if test="${unPublishedDataMsg ne null}">
					<div class='infoMessage' style="display:block">${unPublishedDataMsg}</div>
				</c:if>
			</d:content>
			<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
				<d:content section="${helpIconProvider}">
					<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
					<input type="hidden" id="screenName" value="Competition Configuration" name="screenName"/>
				</d:content>	
			<div class='hr'></div>
			<%-- check for fail or success message --%>
			<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message} <img
					src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('messagediv', this)">
				</div>
			</c:if>
			<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
				<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
				<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
			</c:if>
			<p>Please configure the procurement's competition pools.</p>
			<p></p>
			<div class='formcontainer' id="customQuestion">
				<div class="row">
					 <span class="label" style="height:52px;">Competition Pool Title&#58;</span>
						<span class="formfield">
							<input type="text" name="competitionPoolTitle" class="input agencyClass" maxlength="120" id="competitionPoolTitle" ${readOnlyValue}/>
							<div class='taskButtons'>
								<input type='button' value='Add Competition Pool' class='add' id="addCPButton" disabled/>
							</div>
						</span>
						<span class="formfield error"></span>
				</div>
			</div>
		</d:content>
		<div class='formcontainer' id="customQuestion">
			<div class="row">
				 <span class="label" style="height:178px;">Selected Competition Pools&#58;</span>
					<span class="formfield">
						<select multiple="multiple" style="height:150px;" name="selectedCompetitionPools" class="input agencyClass" id="selectedCompetitionPools">
							<c:forEach var="selectedPoolVar" items="${selectedPool}">
								<option value="${selectedPoolVar}" title="${selectedPoolVar}"  ${readOnlyValue}>${selectedPoolVar}</option>
							</c:forEach>
						</select>
						<div class='taskButtons'>
							<input type='button' value='Remove Competition Pool' class='remove' id="removeCPButton" disabled/>
							</div>
					</span>
					<span class="formfield error">
				</span>
			</div>
			<input type="hidden" name="procurementId" id="procurementId" value="${procurementId}" />
		</div>
		
		<!-- Begin QC8914 R7.2.0 Oversight Role Hide Save Button -->
		<% 
		if(! CommonUtil.hideForOversightRole(request.getSession()))
		{%>
			<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
				<div class="buttonholder">
					<div class='floatLft'>
						<comSM:commonSolicitation screenName="CompetitionConfiguration" level="ProcurementWidget" procurementId="${procurementId}" procurementStatus="${procurementBean.procurementStatus}"></comSM:commonSolicitation>
					</div>
					<c:if test="${(procurementBean.status eq 2) or (procurementAddendumFlag ne 0)}">
						<c:set var="saveButtonSection"><%=HHSComponentMappingConstant.S280_SAVE_BUTTON%></c:set>
						<d:content section="${saveButtonSection}">		
							<input type="submit" value="Save" title='Save changes' id="save"/>
						</d:content>
					</c:if>
				</div>
			</d:content>
		<%} %>
		<!-- End QC8914 R7.2.0 Oversight Role Hide Save Button -->	
			
	</div>
	</form>
</nav:navigationSM>