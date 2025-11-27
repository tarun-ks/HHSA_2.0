<%-- This jsp file has been added in R4 --%>
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
<portlet:actionURL var="evaluationGroupsSummaryAwardURL" escapeXml="false">
	<portlet:param name="action" value="awardContract" />
	<portlet:param name="topLevelFromRequest" value="AwardsandContracts"/>
	<portlet:param name="render_action" value="awardsAndContracts"/>
</portlet:actionURL>
<nav:navigationSM screenName="EvaluationGroupsSummaryAward"> 
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/evaluationGroupsSummaryAward.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
	<form id="evaluationGroupsSummaryAwardForm" name="evaluationGroupsSummaryAwardForm" action="${evaluationGroupsSummaryAwardURL}" method ="post" commandName="EvaluationSummaryBean">
		<input type="hidden" id="procurementId" name="procurementId" value="${procurementBean.procurementId}"/>
		<h2>Evaluation Groups - Awards</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title='Need Help?' onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Evaluation Groups - Awards" name="screenName"/>
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
				<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record will be displayed in Read-only format.</div>
			</c:if>
				<%-- Grid Starts --%>
				<%-- Container Starts --%>
			   <div class="tabularWrapper gridfixedHeight">
			        <st:table objectName="evaluationSummaryList" displayTitle="no" cssClass="heading" 
						alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
						<st:property headingName="Evaluation Group" columnName="evaluationGroupTitle" align="left" size="30%" sortType="evaluationGroupTitle" sortValue="asc">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.EvaluationGroupsSummaryAwardExtension" />
						</st:property>
						<st:property headingName="Closing Date" columnName="submissionCloseDate" align="left" size="20%" sortType="submissionCloseDate" sortValue="asc"/>
						<st:property headingName="# of Awards" columnName="noOfAwards" align="left" size="20%" sortType="noOfAwards" sortValue="asc"/>
						<st:property headingName="Total Award Amount ($)" columnName="awardAmount" align="left" size="30%" sortType="awardAmount" sortValue="asc"/>
					</st:table>
				</div>
			    <%-- Grid Ends --%>
		</form>
</nav:navigationSM>