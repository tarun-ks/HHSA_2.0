<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%-- The jsp is added in Release 7 for Modification Auto Approval Enhancement.
This jsp allows user to enter threshold value for an agency and its providers --%> 
<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.util.DateUtil" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/configureAutoApproval.js"></script>
	
<%-- Included of Jquery CSS and JS files  --%>
<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/accordianFunctions.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/unsaveData.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/autoNumeric-1.7.5.js"></script>
<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/nycMainR2.css" type="text/css"></link>
<portlet:defineObjects />

<%-- resourceURL and hidden variable for ajax call --%>


<portlet:resourceURL var='saveAutoApprovalThreshold' id='saveAutoApprovalThreshold'
	escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${saveAutoApprovalThreshold}'
	id='hiddenSaveThresholdValueURL' />
<portlet:resourceURL var='fetchAutoApprovalThreshold' id='fetchAutoApprovalThreshold'
	escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${fetchAutoApprovalThreshold}'
	id='hiddenFetchAutoApprovalThresholdURL' />

<c:set var="CITY_S404_PAGE"><%=HHSComponentMappingConstant.CITY_S404_PAGE%></c:set>
<d:content section="${CITY_S404_PAGE}"  authorize="">

<form id="agencySettings" name="agencySettings"
	action="<portlet:renderURL/>" method="post">
	<%
	String lsTransactionMsg = "";
	if (null!=request.getAttribute("transactionMessage")){
		lsTransactionMsg = (String)request.getAttribute("transactionMessage");
	}
	if(null!=request.getAttribute("transactionStatus") && "passed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
	<div id="transactionStatusDiv" class="passed" style="display: block"><%=lsTransactionMsg%>
	</div>
	<%}else if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
	<div id="transactionStatusDiv" class="failed" style="display: block"><%=lsTransactionMsg%>
	</div>
	<%}%>

	<div id="transactionStatusDiv" class=""></div>

	<h2 class='autoWidth'>Auto-Approval Maintenance</h2>
	<div class="linkReturnVault alignRht">
		<a
			href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenancelanding&_nfls=false&app_menu_name=header_maintenance"
			>Return to Maintenance
			Main Page</a>
	</div>
	<div class='hr'></div>

	<!-- <p> Select an Agency below to configure the auto-mod approval threshold value for that
		specific Agency.</p> -->
	<div>&nbsp;</div>
	
	<div>Select an Agency below to configure default and custom auto-approval thresholds.</div>
	
	<div class="formcontainer">
		<div class="row">
				<select id="agencySelectBox" name="agencySelectBox"
					class='proposalConfigFormfield' onchange="enableDisableId(this)">
					<option value="Select an Agency" title="Select an Agency">Select
						an Agency</option>
					<c:set var="loagencySettingsBean" value="${agencySettingsBean}"></c:set>
					<c:forEach var="listItems"
						items="${loagencySettingsBean.allAgencyDetailsBeanList}">
						<option value="${listItems.agencyId}"
							title="${listItems.agencyName}">${listItems.agencyName}</option>
					</c:forEach>
				</select>
				<input id="goButton" name="goButton" type="button" class="button" disabled value="Go"
					onclick="javascript:ajaxCallForDefaultThreshold();" />
		</div>
		<div>&nbsp;</div>
		<div class='hr'></div>
	</div>
	<%-- Container Starts --%>	
</form>

<div id=autoApprovalThresholdDiv class="autoApproval">
</div>
</d:content>