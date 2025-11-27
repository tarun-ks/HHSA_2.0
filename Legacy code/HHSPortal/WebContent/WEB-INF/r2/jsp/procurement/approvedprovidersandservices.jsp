<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="comSM" uri="/WEB-INF/tld/commonSolicitation.tld"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page import="javax.portlet.*"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/approvedprovidersandservices.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.tablesorter.js"></script>
<portlet:defineObjects />
<portlet:resourceURL var="fetchAppProviders" id="fetchAppProviders" escapeXml="false">
	<portlet:param  name='procurementId' value='${procurementId}'/>
</portlet:resourceURL>
<portlet:resourceURL var='organizationSummary' id="organizationSummary" escapeXml='false'>
</portlet:resourceURL>
<portlet:actionURL var='backAction' escapeXml='false'>
   	<portlet:param name="submit_action" value="servicesAndProvidersBackAction" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:actionURL>
<portlet:actionURL var='nextAction' escapeXml='false'>
   	<portlet:param name="submit_action" value="servicesAndProvidersNextAction" />
	<portlet:param name="procurementId" value="${procurementId}" />
</portlet:actionURL>

<nav:navigationSM screenName="ServicesAndProviders">
	<div class='floatRht'>
		<comSM:commonSolicitation topLevelStatus="true" procurementId="${procurementId}" providerId="${sessionScope.user_organization}"></comSM:commonSolicitation>
	</div>
	
	<form:form id="appProvAndSericesform" name="appProvAndSericesform" action="${saveApprovedProv}" method ="post" commandName="SelectedServicesBean">
		<input type = "hidden" value='${fetchAppProviders}' id="hiddenApprovedProviders" />
		<input type = "hidden" value='${organizationSummary}' id="hiddenOpenOrganization" />
		<input type = "hidden" value='${pageContext.servletContext.contextPath}' id="contextPathHidden" />
		<input type = "hidden" value="${procurementId}" id="procurementId" name="procurementId" />
		<input type = "hidden" value="${selectionBoxDropDown}" id="selectionBoxDropDown" name="selectionBoxDropDown" />
		<input type = "hidden" value="" id="changeDropDownValue" name="changeDropDownValue" />
		<input type = "hidden" value="${(fn:length(selectedServiceList))}" id="selectedServiceListListSize"/>
		
		<div class=''  id="errorDiv"></div>
		<c:if test="${error ne null }">
			<div class="failedShow">${error}</div>
		</c:if>
		<div class='clear'></div>
		 
		<div id='tabs-container' class='cleaxrHeight'>
		 <%-- Container Starts --%>
		 <div class='clear'></div>
		 
		<%-- Form Data Starts --%>
			<h2>Services and Providers</h2>
			<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Services and Providers" name="screenName"/>
		</d:content>
			<div class='hr'></div>
			<c:set var="numberOfServices" value="${(fn:length(selectedServiceList))}" />
			
			<div>In order to view and to submit a proposal for the RFP when it is released, your organization must have an approved Service Application for one or all (see gray bar below Selected Services) of the Selected Services listed. Providers who are currently eligible to propose are also listed for your reference.  You may use the drop-down menu to modify the list of providers by Service.</div>
			<br>
			<div>Please click <a onclick="pageGreyOut();" class="link" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=provider">here</a> to complete and submit a Service Application if your Provider Status is 'Service App Required' and you wish to receive this RFP upon release. </div>
			<br>
			<div>Please click <a onclick="pageGreyOut();" class="link" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_faq&_nfls=false&removeNavigator=true&action=FAQ&app_menu_name=help_icon">here</a> to view the full Client and Community Services Catalog.</div>
				
			<div class="selectedContainer procurement">
				<div class='clear'><h4 class='floatLft' style='position:static'>Selected Services</h4></div>
				<div class='clear'>
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
			</div>
			
			<div class='generateListPanel' >
				<c:set var="sectionDropDown"><%=HHSComponentMappingConstant.S232_DROP_DOWN%></c:set>
				<d:content section="${sectionDropDown}">	
					<c:choose>
						<c:when test="${(selectionBoxDropDown eq '0')}">
							Providers are required to be approved for all the selected services
						</c:when>
						<c:otherwise>
							Providers are required to be approved for at least one of the selected services.
						</c:otherwise>
					</c:choose>
				</d:content>
			</div>
			<div>&nbsp;</div>
			<div id="approvedProvidersDropDown" class="hiddenBlock">	
				<p>Show providers approved for: 
					<select id="selServiceDropDown">
						<option value="-1">ANY of the services</option>
						<c:forEach var="serviceList" items="${selectedServiceList}">
							<option value="${serviceList.elementId}">${serviceList.serviceName}</option>
						</c:forEach>
					</select>
					Approved Providers: <span class="providerCount"></span>
				</p>
			</div>
			
			<div id="approvedProvidersTableMain" style="min-height:200px;" class="hiddenBlock">
				<div id="approvedProvidersTableContainer">
					<div id="approvedProvidersDiv">
						<div class="tabularWrapper" id="approvedProviderGrid">
							
							<table cellspacing="0" cellpadding="0"  class="grid" id="myTable">
								<thead> 
					             	<tr>
					                	<th><label>Provider Name</label></th>
									</tr>
								</thead>
								<tbody> 
									<c:forEach var="approvedProviderList" items="${approvedProvidersList}"  varStatus="item">
										<tr class="${item.count%2==0?'oddRows':'evenRows'}">
											<td>
												<a  href="javascript: viewOrganizationSummary('${approvedProviderList.organizationId}')">								
													${approvedProviderList.organizationLegalName}
												</a>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
						<div>Approved Providers: <span class="providerCount"></span></div>
					</div>
				</div>
				<input type="hidden" value="${(fn:length(approvedProvidersList))}" id="approvedProviderListSize"/>
			</div>
			<div class="buttonholder clearHeight">
				<input type="button" value="&lt;&lt; Back" onclick="javascript:backButtonAction();"/>
				<c:set var="sectionDropDown"><%=HHSComponentMappingConstant.S232_DROP_DOWN%></c:set>
				<d:content section="${sectionDropDown}">
				<%-- Code updated for R4 Starts --%>
					<c:if test="${((procurementStatus eq '3' ||
									procurementStatus eq '4' ||
									procurementStatus eq '5' ||
									procurementStatus eq '6' ) &&
									(providerStatusId eq '9' ||
									 providerStatusId eq '11' ||
									 providerStatusId eq '12' ||
									 providerStatusId eq '15' ||
									 providerStatusId eq '14' ||
									 providerStatusId eq '13'))
								    || (procurementStatus eq '7' || procurementStatus eq '8')}">
				    	<input type="button" value="Next" onclick="javascript:nextButtonAction();" />
				 	</c:if>
				 	<%-- Code updated for R4 Ends --%>
			 	</d:content>
			 	
			   	<input type="hidden" value="${nextAction}" id="nextPageURL" name="nextPageURL"/>
			   	<input type="hidden" value="${backAction}" id="backPageURL" name="backPageURL"/>
			</div>
		</div>
	</form:form>
	<div class="overlay"></div>
</nav:navigationSM>