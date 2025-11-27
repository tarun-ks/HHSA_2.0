<%-- This JSP was added for R4--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<portlet:defineObjects/>
<nav:navigationSM screenName="Financials">
<d:content section="<%=HHSComponentMappingConstant.S204_SCREEN%>" authorize="" >
	<div id=tabs-container>
		<H2>Financials</H2>
		<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
					<d:content section="${helpIconProvider}">
						<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
						<input type="hidden" id="screenName" value="Financials" name="screenName"/>
					</d:content>	
			<div class=hr></div>
			<%--Satrt of changes for release 3.2.0 enhancement 5684 : check added when pcof task is in progress --%>
			<c:choose>
				<c:when test="${FINANCE_OPEN_ZERO ne null}">
					<div class='infoMessage' style="display:block">${FINANCE_OPEN_ZERO}</div>	
				</c:when>
				<c:otherwise>
					<div class='infoMessage' style="display:block">${FINANCE_SCREEN_CHECK}</div>	
				</c:otherwise>
			</c:choose>
			<%--End of changes for release 3.2.0 enhancement 5684 --%>
	</div>
</d:content>
</nav:navigationSM>