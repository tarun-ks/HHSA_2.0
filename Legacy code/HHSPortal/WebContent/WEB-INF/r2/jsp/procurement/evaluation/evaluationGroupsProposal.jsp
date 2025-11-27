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
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects/>
<nav:navigationSM screenName="EvaluationGroupsSummary"> 
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/evaluationGroupsProposal.js"></script>
<portlet:actionURL var="groupSummaryFormURL" escapeXml="false">
	<portlet:param name="action" value="propEval" />
	<portlet:param name="topLevelFromRequest" value="ProposalsandEvaluations" />
</portlet:actionURL>
	<form id="groupSummaryForm" name="groupSummaryForm" action="${groupSummaryFormURL}" method ="post" commandName="EvaluationGroupsProposalBean">
		<input type="hidden" id="procurementId" name="procurementId" value="${procurementBean.procurementId}"/>
		<h2>Evaluation Groups - Proposals</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title='Need Help?' onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Evaluation Groups - Proposals" name="screenName"/>
		</d:content>	
			<div class='hr'></div>
			<c:if test="${message ne null}">
				<div id="messagediv" class="${messageType}" style="display: block;">
				${message}<img onclick="showMe('messagediv', this)" 
					class="message-close" id="box"
					src="../framework/skins/hhsa/images/iconClose.jpg">
				</div>
			</c:if>
	<%-- Container Starts --%>
				<%-- Grid Starts --%>
			   <div class="tabularWrapper gridfixedHeight">
			        <st:table objectName="evaluationGroupProposal" displayTitle="no" cssClass="heading" 
						alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
						<st:property headingName="Evaluation Group" columnName="evaluationGroupTitle" align="left" size="25%" sortType="evaluationGroupTitle" sortValue="asc">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationGroupProposalExtension" />
						</st:property>
						<st:property headingName="Closing Date" columnName="closingDate" align="left" size="10%" sortType="closingDate" sortValue="asc"/>
						<st:property headingName="Providers Submitted" columnName="providersSubmitted" align="left" size="10%" sortType="providersSubmitted" sortValue="asc"/>
						<st:property headingName="Proposals Submitted" columnName="proposalsSubmitted" align="left" size="10%" sortType="proposalsSubmitted" sortValue="asc"/>
						<st:property headingName="Evaluation Group Status" columnName="evaluationStatus" align="left" size="15%" sortType="evaluationStatus" sortValue="asc"/>
						<st:property headingName="Evaluations In Progress" columnName="evaluationsInProgress" align="left" size="10%" sortType="evaluationsInProgress" sortValue="asc"/>
						<st:property headingName="Evaluations Complete" columnName="evaluationsComplete" align="left" size="10%" sortType="evaluationsComplete" sortValue="asc"/>
					</st:table>
				</div>
			    <%-- Grid Ends --%>
		</form>
	</nav:navigationSM>
	<%-- Overlay Starts --%>
	<div class="overlay"></div>