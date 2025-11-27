<%@page import="java.util.List"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@page import="com.nyc.hhs.model.ProposalDetailsBean"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@page errorPage="/error/errorpage.jsp"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties" />
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<%-- <fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="conceptReportReleaseDt"/>   --%>
<portlet:defineObjects />
<style>
#documentValuePop .label {
	width: 40%;
}

.taskreassign {
	margin-top: -20px;
	margin-top: -10px\9; /* IE8 and below */
}

.paginationWrapper ul li {
	padding-top: 35px;
}
</style>

<portlet:actionURL var="navigateToProcurement" escapeXml="false">
	<portlet:param name="submit_action" value="viewProcurement" />
	<portlet:param name="action" value="proposalDetails" />
</portlet:actionURL>
<input type='hidden' value='${navigateToProcurement}'
	id='navigateToProcurementURL' />


<portlet:actionURL var="navigateToCompetitionPool" escapeXml="false">
	<portlet:param name="submit_action" value="viewCompetitionPool" />
	<portlet:param name="topLevelFromRequest"
		value="ProposalsandEvaluations" />
	<portlet:param name="midLevelFromRequest" value="EvaluationStatus" />
	<portlet:param name="action" value="proposalDetails" />
</portlet:actionURL>
<input type='hidden' value='${navigateToCompetitionPool}'
	id='navigateToCompetitionPoolURL' />
<input type="hidden" id="procurementId" value="" />
<portlet:renderURL var='viewResponse' escapeXml='false'>
	<portlet:param name="action" value="proposalDetails" />
	<portlet:param name="render_action" value="viewResponse" />
	<portlet:param name="showBafoButton" value="true" />
</portlet:renderURL>
<input type='hidden' value='${viewResponse}' id='hiddenViewResponse' />

<portlet:resourceURL var="getProcurementListResourceUrl"
	id="getProcurementListResourceUrl" escapeXml="false">
</portlet:resourceURL>
<input type='hidden' value='${getProcurementListResourceUrl}'
	id='hiddenGetProcurementListResourceUrl' />

<input type='hidden' value='${getProcurementListResourceUrl}'
	id='hiddenGetCompetitionListResourceUrl' />


<portlet:actionURL var="filterProposalList" escapeXml="false">
	<portlet:param name="submit_action" value="filterProposal" />
	<portlet:param name="cityUserSearchProviderId"
		value="${cityUserSearchProviderId}" />
	<portlet:param name="action" value="proposalDetails" />
	<input type="hidden" id="filterProposalListUrl"
		value="${filterProposalList}" />
</portlet:actionURL>

<jsp:include
	page="/portlet/homeprovider/documentshared/shareDocheader.jsp" />
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r5/js/documentshared/proposallist.js"></script>
<form:form id="ProposalFilterForm" name="ProposalFilterForm"
	action="${filterProposalList}" method="post"
	commandName="ProposalDetailsBean"
	onkeydown="if (event.keyCode == 13) {filtertask();}">
	<div class="borderlayout"></div>
	<h2>Proposals</h2>
	<hr align="left" width="90%" style='float:left;'>
	<div class="taskButtons nowrap">
		<span> <input type="button" value="Filter Items" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');" />
		</span class='floatLft'> <span class='totalproposal'>Proposals: ${totalCount}</span>
		<div id="documentValuePop" class='formcontainerFinance'
			style='width: 500px!important;'>
			<div class='close'>
				<a href="javascript:setVisibility('documentValuePop', 'none');">X</a>
			</div>
			<c:if test="${org_type eq 'city_org'}">
				<div class='row'>
					<span class='label'>Agency:</span> <span class='formfield'>
						<form:select path="agencyId" cssClass="widthFull" id="agency"
							onchange="enableDisableDefaultFilter();">
							<form:option id="All NYC Agencies" value="">All NYC Agencies</form:option>
							<c:forEach items="${agencyDetails}" var="agencyDetail">
								<form:option title="${agencyDetail['AGENCY_NAME']}"
									value="${agencyDetail['AGENCY_ID']}">${agencyDetail['AGENCY_NAME']}</form:option>
							</c:forEach>
						</form:select>
					</span>
				</div>
			</c:if>
			<div class="row">
				<span class="label equalForms">Procurement Title:</span> <span
					class="formfield equalForms"> <form:input
						path="procurementtitle" size="32" name="procurementtitle"
						id="procurementtitle" type="text"
						class='procurementConfigDrpdwn widthFull'  maxlength="60"
						onchange="enableDisableDefaultFilter();" />
				</span> <span class="error"></span>
			</div>
			<div class="row">
				<span class="label equalForms">Competition Pool:</span> <span
					class="formfield equalForms"> 
					
					<c:set var="isCompetitionPoolEnable" value="true" />
					<c:if test="${not empty ProposalDetailsBean.competitionPoolTitle}">
						<c:set var="isCompetitionPoolEnable" value="false" />
					</c:if>
					
					<form:input
						path="competitionPoolTitle" name="competitionPoolTitle" type="text"
						class='procurementConfigDrpdwn widthFull' id="competitionPool"
						disabled="${isCompetitionPoolEnable}" maxlength="120"
						onchange="enableDisableDefaultFilter();" />
				</span> <span class="error"></span>
			</div>
			<div class="row">
				<span class="label equalForms">Proposal Title:</span> <span
					class="formfield equalForms"> <form:input
						path="proposalTitle" name="proposalTitle" type="text"
						class='proposalConfigDrpdwn widthFull' id="proposalTitle" title="Enter at least 5 letters of the Proposal Title"
						maxlength="60" onchange="enableDisableDefaultFilter();" />
				</span> <span class="error"></span>
			</div>
			<div class='row'>
				<span class='label'>Status:</span> 
				<span class='formfield'> 
				<span class='leftColumn'> 
					<span>
						<form:checkbox path="proposalStatusList" id='submitted' checked="${(fn:contains(ProposalDetailsBean.proposalStatusList, '18') || firstLoad ) ? 'checked' : ''}" value="18" onchange="enableDisableDefaultFilter();" />
						<label for='submitted'>Submitted</label>
					</span>
					<span>
						<form:checkbox path="proposalStatusList" id='acceptEvaluation' checked="${(fn:contains(ProposalDetailsBean.proposalStatusList, '20') || firstLoad ) ? 'checked' : ''}" value="20" onchange="enableDisableDefaultFilter();" />
						<label for='acceptEvaluation'>Accepted for Evaluation</label>
					</span>
					<span>
						<form:checkbox path="proposalStatusList" id='returnForRevision' checked="${(fn:contains(ProposalDetailsBean.proposalStatusList, '19') || firstLoad) ? 'checked' : ''}" value="19" onchange="enableDisableDefaultFilter();" />
						<label for='returnForRevision'>Returned for Revision</label>
					</span>
					
					<span>
						<form:checkbox path="proposalStatusList" id='evaluated' checked="${(fn:contains(ProposalDetailsBean.proposalStatusList, '21') || firstLoad) ? 'checked' : ''}" value="21" onchange="enableDisableDefaultFilter();" />
						<label for='evaluated'>Evaluated</label>
					</span>
					<span>
						<form:checkbox path="proposalStatusList" id='scoreReturned' checked="${(fn:contains(ProposalDetailsBean.proposalStatusList, '22') || firstLoad) ? 'checked' : ''}" value="22" onchange="enableDisableDefaultFilter();" />
						<label for='scoreReturned'>Scores Returned</label>
					</span>
				</span>
				<span class='rightColumn formfieldright'> 
					<span>
						<form:checkbox path="proposalStatusList" id='selected' checked="${(fn:contains(proposalStatusList.proposalStatusList, '23') || firstLoad) ? 'checked' : ''}" value="23" onchange="enableDisableDefaultFilter();" />
						<label for='chkPendingConfig'>Selected</label>
					</span>
					<span>
						<form:checkbox path="proposalStatusList" id='notSelected' checked="${(fn:contains(proposalStatusList.proposalStatusList, '24') || firstLoad) ? 'checked' : ''}" value="24" onchange="enableDisableDefaultFilter();" />
						<label for='notSelected'>Not Selected</label>
					</span> 
					<span>
						<form:checkbox path="proposalStatusList" id='nonResponsive' checked="${(fn:contains(proposalStatusList.proposalStatusList, '25') || firstLoad) ? 'checked' : ''}" value="25" onchange="enableDisableDefaultFilter();" />
						<label for='nonResponsive'>Non Responsive</label>
					</span>
					
					<span>
						<form:checkbox path="proposalStatusList" id='reassignment' checked="${(fn:contains(proposalStatusList.proposalStatusList, '141') || firstLoad) ? 'checked' : ''}" value="141" onchange="enableDisableDefaultFilter();" />
						<label for='reassignment'>Pending Reassignment</label>
					</span>
				</span>
				</span>
			</div>
			<div class='row'>
				<span class='label'>Last Modified Date From:</span> <span
					class='formfield'> <span class='floatLft'> <form:input
							path="modifiedDateFrom" id="activeProposalFrom"
							validate="calender" cssClass="datepicker" maxlength="10"
							onchange="enableDisableDefaultFilter();" /> <img
						src="../framework/skins/hhsa/images/calender.png"
						onclick="NewCssCal('activeProposalFrom',event,'mmddyyyy');return false;" />
						&nbsp; &nbsp;
				</span> <span id="activeProposalFromError" class="error clear"></span> to:

					<span> <form:input path="modifiedDateTo"
							id="activeProposalTo" validate="calender" cssClass="datepicker"
							maxlength="10" onchange="enableDisableDefaultFilter();" /> <img
						src="../framework/skins/hhsa/images/calender.png"
						onclick="NewCssCal('activeProposalTo',event,'mmddyyyy');return false;" />
				</span> <span id="activeProposalToError" class="error clear"></span>
				</span>
			</div>
			<div class="buttonholder">
				<input type="button" id="clearFilterBtn" class="graybtutton"
					value="Set To Default Filter" disabled="disabled"
					onclick="clearfilter();" /> <input type="button" value="Filter"
					name="filter" id='filtersBtn' onclick='filtertask()' />
			</div>
		</div>
	</div>

	<form:hidden path="firstSort" />
	<form:hidden path="firstSortDate" />
	<form:hidden path="secondSort" />
	<form:hidden path="secondSortDate" />
	<form:hidden path="firstSortType" />
	<form:hidden path="secondSortType" />
	<form:hidden path="sortColumnName" />

</form:form>

<div class='tabularWrapper budgetListDiv'
	style='min-height: 700px !important'>

	<st:table objectName="proposalList" cssClass="heading"
		alternateCss1="evenRows" alternateCss2="oddRows"
		pageSize='${allowedObjectCount}'>

		<c:if test="${org_type eq 'city_org'}">
			<st:property headingName="Agency" columnName="agencyId" size="5%"
				sortType="agencyId">
				<st:extension
					decoratorClass="com.nyc.hhs.frameworks.grid.ProposalExtension" />
			</st:property>
		</c:if>
		<st:property headingName="Procurement Title"
			columnName="procurementtitle" size="15%" sortType="procurementtitle">
			<st:extension
				decoratorClass="com.nyc.hhs.frameworks.grid.ProposalExtension" />
			<!-- modelandview -->
		</st:property>
		<st:property headingName="Competition Pool"
			columnName="competitionPoolTitle" size="15%"
			sortType="competitionPoolTitle">
			<st:extension
				decoratorClass="com.nyc.hhs.frameworks.grid.ProposalExtension" />
		</st:property>

		<st:property headingName="Proposal ID" columnName="proposalId"
			size="11%" sortType="proposalId">
			<st:extension
				decoratorClass="com.nyc.hhs.frameworks.grid.ProposalExtension" />
		</st:property>
		<st:property headingName="Proposal Title" columnName="proposalTitle"
			size="19%" sortType="proposalTitle">
			<st:extension
				decoratorClass="com.nyc.hhs.frameworks.grid.ProposalExtension" />
		</st:property>

		<st:property headingName="Proposal Status"
			columnName="proposalStatusId" size="20%" sortType="proposalStatusId">
			<st:extension
				decoratorClass="com.nyc.hhs.frameworks.grid.ProposalExtension" />
		</st:property>
		<st:property headingName="Last Modified" columnName="modifiedDate"
			size="20%" sortType="modifiedDate">
			<st:extension
				decoratorClass="com.nyc.hhs.frameworks.grid.ProposalExtension" />
		</st:property>
	</st:table>

	<c:if test="${fn:length(proposalList) eq 0}">
		<div class="noRecordPaymentDiv noRecord" id="noRecordPaymentDiv">No proposals submitted by this provider</div>
	</c:if>
	<div class='floatLft'>
		<span> Proposals: <label>${totalCount}</label></span>
	</div>
</div>
