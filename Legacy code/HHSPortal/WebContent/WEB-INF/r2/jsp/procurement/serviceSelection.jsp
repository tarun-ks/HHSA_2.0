<%-- This page will be displayed when a user click on the add service button.It display  list of services that can be selected.--%>
<%@page import="com.nyc.hhs.model.TaxonomyServiceBean"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="comSM" uri="/WEB-INF/tld/commonSolicitation.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:defineObjects />
<nav:navigationSM screenName="ServiceSelection"> 	
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/serviceSelection.js"></script>
<style type="text/css">
	.returnButton, .searchResultsContainer, .displayNone{
		display:none;
	}
	.individualError{
		float: none;
	}
	.titleClass{
		color: #666;
	    float: left;	   
	    padding: 4px 8px 4px 0;
	    width: 12%;
	    word-wrap: break-word;	    
	    text-align: right;
	    font-weight: bold;
	}
	.descriptionTreeClass {
	    color: #666;
	    float: left;	    
	    padding: 4px 10px 4px 8px;
	     width: 77%;
	    /*word-break: break-all;*/	     
	    position: relative;	   
	    min-height: 28px;	
	    word-wrap: break-word;    
	}
	.descriptionTreeClassAgency {
	    color: #666;
	    float: left;	    
	    padding: 4px 10px 4px 8px;
	     width: 85%;
	    /*word-break: break-all;*/	     
	    position: relative;	   
	    min-height: 28px;	
	    word-wrap: break-word;    
	}
	.descriptionTreeClass input {
	    float: right;
	    position: absolute;
	    right: -72px;
	    top: 4px;
	}
	.descriptionTreeClass .ulTreeClass {
		float:left;
	}
	.liTreeClass { 
	    clear: both;
	    border-bottom: 1px solid #E4E4E4;
	}
	.iconQuestion{
		margin-left: 6px;
	}
	.col3{
		width: auto;
		text-align: right;
	}
</style>

<portlet:actionURL var="viewProcurementUrl" escapeXml="false">
	<portlet:param name="submit_action" value="processServiceAction"/>
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
	<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}"/>	
</portlet:actionURL>

<portlet:renderURL var="backTOProcurementServiceURL" escapeXml="false">
	<portlet:param name="render_action" value="viewProcurement"/>
	<portlet:param name="procurementId" value="${procurementId}"/>
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
	<portlet:param name="midLevelFromRequest" value="ProcurementSummary"/>
</portlet:renderURL>

<input type="hidden" name="backTOProcurementServiceURL" value="${backTOProcurementServiceURL}" id="backTOProcurementServiceURL"/>


<form:form id="seviceSelectionForm" action="${viewProcurementUrl}" method="post"
	commandName="Procurement" name="seviceSelectionForm">
	
<input type="hidden" value="${isPrcoCancelled}" id="isCancelledProcurement"/>
<div id='tabs-container'>
<d:content isReadOnly="${(accessScreenEnable eq false) or hideExitProcurement and (procurementBean.status ne 7 and procurementBean.status ne 8)}" readOnlyHref="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
	<input type="hidden" name="actionURL" value="<portlet:actionURL/>" id="actionURL"/>	
	<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
	<input type="hidden" name="procurementId" value="${procurementId}" />
	<input type="hidden" name="org_type" value="${org_type}" id="org_type"/>
	 <h2 style='width: 72%;'>Service Selection</h2> 
	<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title='Need Help?'  onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Services and Providers" name="screenName"/>
		</d:content>	
		<div class='hr'></div>
		<c:set var="sectionUnpublishedInformation"><%=HHSComponentMappingConstant.S206_UNPUBLISHED_INFORMATION%></c:set>
		<d:content section="${sectionUnpublishedInformation}">	
			<c:if test="${unPublishedDataMsg ne null}">
				<div class='infoMessage' style="display:block">${unPublishedDataMsg}</div>
			</c:if>
		</d:content>
			
	<c:if test="${message ne null}">
		<div class="${messageType}" id="messagediv" style="display:block">${message} <img
			src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
			class="message-close" 
			onclick="showMe('messagediv', this)">
		</div>
	</c:if>
		<div class="failed" id="errorMessage">You must select at least one service.<img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('errorMessage', this)">
				</div>
		<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
			<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
			<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
		</c:if>
		<div class="overlay"></div>
		<div class="alert-box-contact">
			<div class="content">
				<div id="newTabs">
					<div id="contactDiv"></div>
				</div>
			</div>
		</div>
		
		<input type="button"  class="floatRht returnButton" name="returnButton" value="Return"/>
		<div class='clear'></div>
		<div id="addServiceText">
		   <p>Below is a full list of Services for you can add to this Procurement. Services are grouped by category. 
		   To add a Service, click the "Add" button or the "Continue" button to view more Services.</p>
		   <p>You must add at least one Service to complete your HHS Accelerator Procurement. If multiple services are added, 
		   you will be required to indicate if the providers must be approved in ALL the services or at least one of the services in 
		   order to respond to the RFP. </p>
		</div>

	<c:forEach var="serviceName" items="${loElementIdList}">
		<div id='${serviceName.elementId}' class="messagediv failed" style="display: block;">
	    		${serviceName.elementName} can no longer be used for Procurements due to taxonomy changes. You must remove this 
				service from the Service Selection screen
				<img onclick="showMe('${serviceName.elementId}', this)" 
						class="message-close" id="box" src="../framework/skins/hhsa/images/iconClose.jpg">
		</div>
	</c:forEach>

		<div id="searchServiceText" style="display:none">
			<p>Below are the search results for the keyword(s) you entered. Click the "Add" button next each Service to 
					add it to the list of serviceServices your organization would like to apply for.</p>
		   	<p>Once all the Services have been searched for and added to the "Selected Services" table, click the "Return" button 
					on the top right corner of the page. </p>
		</div>
		<div class='evenRows floatRht' style='margin-top:-11px;'>
			<c:choose>
				<c:when test="${org_type ne 'agency_org' and !isPrcoCancelled}">
					<a href="javascript:;" id="removeAll" class="displayNone" onclick="removeAllServices();"><b>Remove All</b></a>
				</c:when>
			</c:choose>
		</div>
<c:set var="divClass" value="selectedContainer"/>
<c:if test="${org_type eq 'agency_org'}">
	<c:set var="divClass" value="selectedContainerAgency"/>
</c:if>		
		<div class="${divClass}">
			<h4>Selected Services</h4>
			<input type="hidden" id="noneHidden" value="${(fn:length(selectedServiceList))}"/>
			<ul id="selected_Services">
				<c:choose>
					<c:when test="${org_type eq 'agency_org' or isPrcoCancelled}">
						<span>
							<c:forEach var="selectedItems" items="${selectedServiceList}" varStatus="item">
								<input type="hidden" id="hiddenSelectedServices" value="${selectedItems.elementId}" />
								<li id="displayService${selectedItems.elementId}">${selectedItems.serviceName}</li>
							</c:forEach>
						</span>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${isSave ne null and isSave eq 'true'}">
								<c:forEach var="selectedItems" items="${selectedServiceList}">
									<input type="hidden" id="hiddenSelectedServices" value="${selectedItems.elementId}" />
									<li onclick="hideShowDisplayService(this,'${selectedItems.elementId}')" id="displayService${selectedItems.elementId}">
										${selectedItems.serviceName}</li>
								</c:forEach>	
							</c:when>
							<c:otherwise>
								<c:forEach var="selectedItems" items="${selectedServiceList}">
									<input type="hidden" id="hiddenSelectedServices" value="${selectedItems.elementId}" />
									<li onclick="hideShowDisplayService(this,'${selectedItems.elementId}')" id="displayService${selectedItems.elementId}">
										${selectedItems.serviceName}</li>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
								
				<li class="noneSelected" id="noneSelected" style="display:none">None selected...</li>
			</ul>
		</div>
		<div class="hr"></div>	
		<%-- Selected Container end --%>
			
		<%--  add service block --%>
		<div class="addServiceBlock">
			<h3 style="display: inline;" >Select from Full List</h3><br />
			<div class="expandCollapseLink">
			    <a href="#"  onclick="collapseExpandAll('collapseAll');return false;">Collapse all</a> |
			    <a href="#"  onclick="collapseExpandAll('expandAll');return false;">Expand all</a>
			</div>
			
			<div>${finalTreeAsString}</div>      			
			<div id="displayContinue">${finalTreeAsString}</div>
			<div style="display:none">
				<div id="tempId" style="display:none"></div>
			</div>
		</div>
		

	<%--  add buttons --%>			
	<div class="buttonholder">
	<div class='floatLft'>
	<comSM:commonSolicitation screenName="ServiceSelection" level="ProcurementWidget" procurementId="${procurementId}" procurementStatus="${procurementBean.procurementStatus}"></comSM:commonSolicitation>
	</div>


	<input type="hidden" name="selectedService" value="" id="addSelectedServices">
	<c:set var="sectionCancelProcurement"><%=HHSComponentMappingConstant.S206_SAVE_BUTTON%></c:set>
	<d:content section="${sectionCancelProcurement}">
		<c:if test="${saveButtonStatus and !isPrcoCancelled}">
			<input name="saveService" type="submit" class="button" value="Save" title="Save changes" id="saveButton" onclick="return setValue('saveServices', event)"/>
		</c:if>
	</d:content>
	
	<input type="hidden" name="next_action" value="" id="saveServices">
				
</div>
	<%--  add buttons finish--%>
</d:content>
</div>
</form:form>
<form name="hiddenFormUrl" action="${hiddenFormUrl}" method="post">
		<div id="hiddenDiv">
			<input type="hidden" value="${topLevelFromRequest}" id="topLevelFromRequest" name="topLevelFromRequest"/>
			<input type="hidden" id="midLevelFromRequest" name="midLevelFromRequest"/>
			<input type="hidden" id="hiddenFormUrlNoAction" name="hiddenFormUrlNoAction" value="${hiddenFormUrlNoAction}"/>
			<input type="hidden" value="${procurementId}" id="procurementId" name="procurementId"/>
		</div>
</form>   			
<script type="text/javascript">
	setSelectedServices(null);
</script>
</nav:navigationSM>
<div class="overlay"></div>
