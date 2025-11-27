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
<portlet:actionURL var="selectionDetailsSummaryURL" escapeXml="false">
	<portlet:param name="action" value="selectionDetail" />
	<portlet:param name="topLevelFromRequest" value="SelectionDetails"/>
	<portlet:param name="render_action" value="viewSelectionDetails"/>
</portlet:actionURL>
<nav:navigationSM screenName="SelectionDetailsSummary"> 
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/selectionDetailsSummary.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
	<form id="selectionDetailsSummaryForm" name="selectionDetailsSummaryForm" action="${selectionDetailsSummaryURL}" method ="post" commandName="SelectionDetailsSummaryBean">
		<input type="hidden" id="procurementId" name="procurementId" value="${procurementBean.procurementId}"/>
		<h2>Selection Details Summary</h2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title='Need Help?' onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Selection Details Summary" name="screenName"/>
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
		<p>Select an award from the list below to view more details.</p>
				<%-- Grid Starts --%>
				<%-- Container Starts --%>
			   <div class="tabularWrapper gridfixedHeight">
			        <st:table objectName="selectionDetailsSummaryList" displayTitle="no" cssClass="heading" 
						alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
						<st:property headingName="Competition Pool" columnName="competitionPoolTitle" align="left" size="25%" sortType="competitionPoolTitle" sortValue="asc">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.SelectionDetailsSummaryExtension" />
						</st:property>
						<st:property headingName="Award E-PIN" columnName="awardEpin" align="left" size="15%" sortType="awardEpin" sortValue="asc"/>
						<st:property headingName="CT#" columnName="contractNumber" align="left" size="15%" sortType="contractNumber" sortValue="asc"/>
						<%-- Start : Changes in R5 --%>
						<st:property headingName="Amount ($)" columnName="amount" align="left" size="20%" sortType="amount" sortValue="asc">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.SelectionDetailsSummaryExtension" />
						</st:property>
						<st:property headingName="Contract Status" columnName="contractStatus" align="left" size="25%" sortType="contractStatus" sortValue="asc">
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.SelectionDetailsSummaryExtension" />
						</st:property>
						<%-- End : Changes in R5 --%>
					</st:table>
				</div>
			    <%-- Grid Ends --%>
		</form>
</nav:navigationSM>