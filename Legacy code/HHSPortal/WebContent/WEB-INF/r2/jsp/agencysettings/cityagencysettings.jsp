<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>

<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/agencySettings.js"></script>

<portlet:defineObjects />

<%-- resourceURL and hidden variable for ajax call --%>
<portlet:resourceURL var='fetchLevelsOfReview' id='fetchLevelsOfReview'
	escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${fetchLevelsOfReview}'
	id='hiddenLevelsOfReviewURL' />

<portlet:resourceURL var='saveLevelsOfReview' id='saveLevelsOfReview'
	escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${saveLevelsOfReview}'
	id='hiddenSaveLevelsOfRevURL' />


<c:set var="CITY_S404_PAGE"><%=HHSComponentMappingConstant.CITY_S404_PAGE%></c:set>
<d:content section="${CITY_S404_PAGE}"  authorize="">

<form id="agencySettings" name="agencySettings"
	action="<portlet:resourceURL/>" method="post">
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

	<h2 class='autoWidth'>Agency Settings</h2>
	<div class="linkReturnVault alignRht">
		<a
			href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenancelanding&_nfls=false&app_menu_name=header_maintenance"
			>Return to Maintenance
			Main Page</a>
	</div>
	<div class='hr'></div>

	<p>There are several review processes that all agencies will be
		expected to complete via tasks. Select an Agency and Review Process
		below to configure the number of levels of review required for that
		specific Agency and Review Process.</p>
	<div>&nbsp;</div>

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
		</div>
		<div class="row">
			<select id="reviewProcessSelect" name="reviewProcessSelect"
				class='proposalConfigFormfield' disabled
				onchange="enableDisableId(this)">
				<option value="rps0" title="Select a Review Process">Select
					a Review Process</option>
				<c:forEach var="listItems"
					items="${loagencySettingsBean.allReviewProcessBeanList}">
					<option value="${listItems.reviewProcessId}"
						id="${listItems.reviewProcessDescription}"
						title="${listItems.reviewProcess}">${listItems.reviewProcess}</option>
				</c:forEach>
			</select> <input id="goButton" name="goButton" type="button" class="button"
				disabled value="Go"
				onclick="javascript:ajaxCallForLevelsOfReview();" />
		</div>
	</div>

	<div>&nbsp;</div>

	<%-- Container Starts --%>
	<div id=levelsOfReviewDiv style="display: none">
		<h4 class='generateListPanel agencyGreyTitle'><span id="levelofRevw" class="bold"></span></h4>

		<div id='tabs-container'>
			<h3>Description:</h3>

			<p id="reviewProcDesc"></p>

			<div class='hr'></div>

			<h3>Select Levels of Review</h3>
			<p>
				Please select how many levels of reviews 
				<label id="sp1" class="bold"></label>&nbsp;will need to fully complete the <label id="sp2" class="bold"></label>&nbsp;process and click Save.
			</p>
			<%-- Release 5 changes starts--%>
			<ul id="NumberOfLevels">
			<%-- Release 5 changes Ends--%>
				<li><input name="rdoSettings" value="2" type="radio"
					id='rdoSettings1' onchange="javascript:hideTransactionStatusDiv();" /><label
					for='rdoSettings1'><span id="sp3" class="bold""></span>
					will need <b>2</b> levels of <span id="sp6"></span> Review</label>
				</li>
				<li><input name="rdoSettings" value="3" type="radio"
					id='rdoSettings2' onchange="javascript:hideTransactionStatusDiv();" /><label
					for='rdoSettings2'><span id="sp4" class="bold"></span>
					will need <b>3</b> levels of <span id="sp7"></span> Review</label>
				</li>
				<li><input name="rdoSettings" value="4" type="radio"
					id='rdoSettings3' onchange="javascript:hideTransactionStatusDiv();" /><label
					for='rdoSettings3'><span id="sp5" class="bold"></span>
					will need <b>4</b> levels of <span id="sp8"></span> Review</label>
				</li>
			</ul>

			<div class="buttonholder">
				<input id="saveLevels" name="saveLevels" type="button"
					class="button" value="Save" disabled='disabled'
					onclick="javascript:ajaxCallToSaveReviewLevels();" />
			</div>
		</div>

	</div>
</form>
<%-- Release 5 changes starts--%>
</d:content>
<%-- Release 5 changes Ends--%>