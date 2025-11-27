<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="rule" uri="/WEB-INF/tld/rule-taglib.tld"%>
<%@ taglib prefix="comSM" uri="/WEB-INF/tld/commonSolicitation.tld"%>
<%@ taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page import="javax.portlet.*"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/approvedProviders.js"></script>
<portlet:defineObjects />
<portlet:resourceURL var="fetchAppProviders" id="fetchAppProviders" escapeXml="false">
	<portlet:param  name='procurementId' value='${procurementId}'/>
</portlet:resourceURL>
<portlet:resourceURL var='organizationSummary' id="organizationSummary" escapeXml='false'>
</portlet:resourceURL>
<portlet:actionURL var="saveApprovedProv" escapeXml="false">
	<portlet:param name="submit_action" value="saveApprovedProviders"/>
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
</portlet:actionURL>
<nav:navigationSM screenName="ApprovedProviders">
	<c:if test="${isAjax eq null || isAjax ne 'true' }">
		<rule:Rule ruleId="DisableGenerateListButton" requestAttName="statusChannel">
			<c:set var="generateButtonStatus" value="true"></c:set>
		</rule:Rule>
		<rule:Rule ruleId="DisableSelectionBoxDropDown" requestAttName="statusChannel">
			<c:set var="dropDownStatus" value="true"></c:set>
			<c:set var="saveButtons" value="true"></c:set>
		</rule:Rule>
	</c:if>
	<d:content isReadOnly="${(accessScreenEnable eq false) or hideExitProcurement and (procurementBean.status ne 7 and procurementBean.status ne 8)}" readOnlyHref="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
	<form:form id="appProvform" name="appProvform" action="${saveApprovedProv}" method ="post" commandName="SelectedServicesBean">
		<input type = "hidden" value='${fetchAppProviders}' id="hiddenApprovedProviders" />
		<input type = "hidden" value='${organizationSummary}' id="hiddenOpenOrganization" />
		<input type = "hidden" value='${pageContext.servletContext.contextPath}' id="contextPathHidden" />
		<input type = "hidden" value="${SelectedProviderValue}" id="provDropDownOption" name="provDropDownOption" />
		<input type = "hidden" value="${procurementId}" id="procurementId" name="procurementId" />
		<div class='clear'></div>
		<div id='tabs-container'>
		 <%-- Container Starts --%>
		 <div class='clear'></div>
		 
		<%-- Form Data Starts --%>
			<h2>Approved Providers</h2>
			
			<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Services and Providers" name="screenName"/>
		</d:content>	
			<div class='hr'></div>
			<c:if test="${error ne null and error eq 'true'}">
				<div class="failedShow">
					<fmt:message key="approvedProvider.statusChange"/>
				</div>
			</c:if>
			<c:set var="numberOfServices" value="${(fn:length(selectedServiceList))}" />
			<input type="hidden" id="numberOfServices" name="numberOfServices" value="${numberOfServices}"/>
			<c:set var="sectionDropDown"><%=HHSComponentMappingConstant.S207_UNPUBLISHED_INFORMATION%></c:set>
			<d:content section="${sectionDropDown}">
				<c:if test="${unPublishedDataMsg ne null}">
					<div class='infoMessage' style="display:block">${unPublishedDataMsg}</div>
				</c:if>
			</d:content>
			<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
				<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
				<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
			</c:if>
			<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('messagediv', this)">
				</div>
			</c:if>
			<c:forEach var="serviceName" items="${evidenceServiceList}" varStatus="item">
				<div id='messagediv${item.index}' class="messagediv failed" style="display: block;">
				     ${serviceName} can no longer be used for Procurements due to taxonomy changes. Please go to the Service Selection screen to remove this service the indicated service(s).
					<img
						onclick="showMe('messagediv${item.index}', this)" 
						class="message-close" id="box"
						src="../framework/skins/hhsa/images/iconClose.jpg">
				</div>
			</c:forEach>
			<p>Services currently selected for the Procurement are displayed below. To view Providers with approved Service Applications who are eligible to submit proposals to the RFP, please select an option from the drop down menu and generate your list.</p>
			<div class="selectedContainer procurement">
				<div class='clear'><h4 class='floatLft' style='position:static'>Selected Services</h4></div>
				<div class='clear' style='height: 122px; overflow: auto'>
					<c:choose>
						<c:when test="${!empty selectedServiceList}">
							<span>
								<c:set var="serviceElementIdList" value="" />
								<c:forEach var="serviceList" items="${selectedServiceList}"  varStatus="item">
									<label><c:out value="${serviceList.serviceName}"/>;</label>
									<c:set var="serviceElementIdList" value="${serviceList.elementId},${serviceElementIdList}" />
									<c:if test="${item.index ne 1 && ((item.index+1)%6) eq 0}">
										</span><span>										
									</c:if>
								</c:forEach>
							</span>
							<input type="hidden" name="serviceElementIdList" value="${serviceElementIdList}" id="serviceElementIdList"/>
						</c:when>
						<c:otherwise>
							<div id="ServicesStatusDiv" style="display:block" >No services have been selected. Return to the Service Selection tab to identify services for this RFP</div>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			
			<div class='generateListPanel' >
				<c:choose>
					<c:when test="${selectedType eq '1'}">
						<c:set var="eq1" value=" selected=selected" />
						<c:set var="eq0" value="" />
					</c:when>
					<c:when test="${selectedType eq '0'}">
						<c:set var="eq0" value=" selected=selected" />
						<c:set var="eq1" value="" />
					</c:when>
				</c:choose>
				<c:set var="sectionDropDown"><%=HHSComponentMappingConstant.S207_DROP_DOWN%></c:set>
				<d:content section="${sectionDropDown}">
				Providers are required to be approved for:
						<c:set var="sectionAgency"><%=HHSComponentMappingConstant.S207_AGENCY_SECTION%></c:set>
						<d:content section="${sectionAgency}">
							<select name="selectionBoxDropDown" id="selectionBoxDropDown" disabled="disabled">
								<option value="1" ${eq1}>at least one of the selected services</option>
								<option value="0" ${eq0}>all selected services</option>
							</select>
						</d:content>
						<c:set var="sectionAccelerator"><%=HHSComponentMappingConstant.S207_CITY_SECTION%></c:set>
						<d:content section="${sectionAccelerator}">
							<c:choose>
							<c:when test="${dropDownStatus ne true or numberOfServices eq 0 }">
								<select name="selectionBoxDropDown" id="selectionBoxDropDown" disabled="disabled">
									<option value="1" ${eq1}>at least one of the selected services</option>
									<option value="0" ${eq0}>all selected services</option>
								</select>
							</c:when>
							<c:otherwise>
								<select name="selectionBoxDropDown" id="selectionBoxDropDown">
									<option value="1" ${eq1}>at least one of the selected services</option>
									<option value="0" ${eq0}>all selected services</option>
								</select>
							</c:otherwise>
						</c:choose>
					</d:content>
					<input type="hidden" value="${selectedType}" id="selectionBoxDropDownHidden"/>
					<c:choose>
						<c:when test="${(generateButtonStatus ne true) or (numberOfServices eq 0)}">
							<input type="button" class="button" disabled="disabled" value="Generate List" />
						</c:when>
						<c:otherwise>
							<input type="button" class="button" value="Generate List" onclick="javascript:approvedProviderGenerate();"/>
						</c:otherwise>
					</c:choose>
				</d:content>
			</div>
			<div>&nbsp;</div>
			<div id="approvedProvidersDropDown" class="hiddenBlock">	
				<p>Show providers approved for: 
					<select id="selServiceDropDown">
						<option value="-1">ANY of the services</option>
						<c:forEach var="serviceList" items="${portletSessionScope.selectedServiceList}">
							<option value="${serviceList.elementId}">${serviceList.serviceName}</option>
						</c:forEach>
					</select>
				</p>
			</div>
			<div id="approvedProvidersCount" class="hiddenBlock">	
				Approved Providers: <span class="providerCount"></span>
			</div>
			<div id="approvedProvidersTableMain" style="min-height:200px;">
				<div id="approvedProvidersTableContainer">
				<c:set var="sectionNoProviderCriteria"><%=HHSComponentMappingConstant.S207_NO_PROVIDER_CRITERIA%></c:set>
					<d:content section="${sectionNoProviderCriteria}">
						<c:if test="${empty approvedProvidersList}" >
							<div id="providerStatusDiv" class="failedShow">No providers match your criteria</div>
						</c:if>
						<c:if test="${noApprovedProvs eq true}" >
							<div class="failedShow">No providers match your criteria</div>
						</c:if>
					</d:content>
					<c:if test="${!empty approvedProvidersList}" >
						<div id="approvedProvidersDiv">
							<div class="tabularWrapper">
								<st:table objectName="approvedProvidersList"  cssClass="heading"
									alternateCss1="evenRows" alternateCss2="oddRows">
									<st:property headingName="Provider Name" columnName="organizationLegalName" align="center" sortType="organizationLegalName" sortValue="asc"
										size="34%">
										<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.OrganizationNameExtension" />
									</st:property>
									<st:property headingName="Business Application Expiration Date" columnName="baExpDate" sortType="baExpDate" sortValue="asc"
										align="left" size="33%">
										<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.OrganizationNameExtension" />
									</st:property>	
									<st:property headingName="Filings Expiration Date" columnName="filingExpDate" sortType="filingExpDate" sortValue="asc"
										align="left" size="33%">
										<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.OrganizationNameExtension" />
									</st:property>
								</st:table>			
							</div>
							<div>Approved Providers: <span class="providerCount"><c:out value="${(fn:length(approvedProvidersList))}"/></span></div>
						</div>
					</c:if>
					<input type="hidden" value="${(fn:length(approvedProvidersList))}" id="approvedProviderListSize"/>
				</div>
			</div>
			<div class="buttonholder">
				<div class='floatLft'>
					<comSM:commonSolicitation screenName="ApprovedProviders" level="ProcurementWidget" procurementId="${procurementId}" procurementStatus="${procurementBean.procurementStatus}"></comSM:commonSolicitation>
				</div>
				<c:set var="sectionSaveButton"><%=HHSComponentMappingConstant.S203_SAVE_BUTTON%></c:set>
				<d:content section="${sectionSaveButton}">
					<c:if test="${saveButtons eq true and numberOfServices > 0}">
						<input type="submit" value="Save" title='Save changes' id="saveChanges" onclick="setPageGreyOut()"/>
					</c:if>
				</d:content>
			</div>
		</div>
	</form:form>
	</d:content>
</nav:navigationSM>
<div class="overlay"></div>