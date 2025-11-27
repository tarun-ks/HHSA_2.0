<%-- This jsp  redirects the user to the specified tab--%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/headerLevelJS.js"></script>
<style type="text/css">
        #greyedBackgroundR2 {
            position: fixed;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            margin: auto;
            margin-top: 0px;
            width: 100%;
            height: 100%;
            background : none repeat scroll 0 0 #fff;
            z-index: 9999;
			opacity: 0.8;
			filter: alpha(opacity = 80);
			text-align:center;
			vertical-align:middle
        } 
 #greyedBackgroundR2 img{
	background-color:#fff;
	top:48%;
	position:relative;
	opacity: 0.8;
 }
</style>
<portlet:defineObjects />
<h2 class='autoWidth'>
	<%-- choose tag starts which displays text on the basis of org_type--%>
	<c:choose>
		<c:when test="${org_type ne 'provider_org'}">
			Manage Procurement:
				<c:choose>
					<c:when test="${procurementBean.lastUpdatedDate ne null}">
						<span>${procurementBean.procurementTitle}</span>
					</c:when>
					<c:otherwise>
						<span>New Procurement</span>				
					</c:otherwise>
				</c:choose>
		</c:when>
		<c:otherwise>
			Procurement:
				<span>${procurementBean.procurementTitle}</span>
		</c:otherwise>
	</c:choose>
	<%-- choose tag ends--%>
</h2>
<c:if test="${hideExitProcurement ne true}">
	<div class='linkReturnValut'><a id="exitProcurementAnchor"  href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_procurement&_nfls=false&app_menu_name=header_procurement&filtered=filtered">Exit Procurement</a></div>
</c:if>
<div class="clear"></div>
<%-- Application Main Navigation Section Starts --%>
<div class="appnavbar">
	<%--City/Agency links start--%>
	<%--ProcurementInformation links start--%>
	<c:if test="${ProcurementInformation ne null and (ProcurementInformation.tabState eq 'E' or ProcurementInformation.tabState eq 'D')}">
	 	<ul class="roundcorners">
	        <li class="<c:if test="${ProcurementInformation.selected eq true}">firstcurrent</c:if> <c:if test="${ProcurementInformation.tabState eq 'D'}">tabDisabled</c:if> nobdr" id="ProcurementInformation">
	        	<a class="navigationR2Class" href="#" >
	        		<c:if test="${ProcurementInformation.tabState ne 'D'}">
						<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementInformation" />
						<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ProcurementSummary" />
						<input type="hidden" name="render_action" id="render_action" value="viewProcurement" />
					</c:if>
					Procurement Information
				</a>
	        </li>
		</ul>
	</c:if>
	<%--ProcurementInformation links ends--%>
	<%--ProcurementRoadmapDetails links start--%>
	<c:if test="${ProcurementRoadmapDetails ne null and (ProcurementRoadmapDetails.tabState eq 'E' or ProcurementRoadmapDetails.tabState eq 'D')}">
		<ul class="roundcorners">
	        <li class="<c:if test="${ProcurementRoadmapDetails.selected eq true}">firstcurrent</c:if> <c:if test="${ProcurementRoadmapDetails.tabState eq 'D'}">tabDisabled</c:if> nobdr" id="ProcurementRoadmapDetails">
	        	<a class="navigationR2Class" href="#" >
	        		<c:if test="${ProcurementRoadmapDetails.tabState ne 'D'}">
	        			<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementRoadmapDetails" />
						<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ProcurementSummary" />
						<input type="hidden" name="render_action" id="render_action" value="viewProcurement" />
					</c:if>
					Procurement Roadmap Details
				</a>
	        </li>
		</ul>
	</c:if>
	<%--ProcurementRoadmapDetails links ends--%>
	<%--RFPDetails links start--%>
	<c:if test="${RFPDetails ne null and (RFPDetails.tabState eq 'E' or RFPDetails.tabState eq 'D')}">
	    <ul class="roundcorners">
	       	<li class="<c:if test="${RFPDetails.selected eq true}">firstcurrent</c:if> <c:if test="${RFPDetails.tabState eq 'D'}">tabDisabled</c:if> nobdr" id="RFPDetails">
	       		 <a class="navigationR2Class" href="#" >
	       		 <c:if test="${RFPDetails.tabState ne 'D'}">
					<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="RFPDetails" />
					<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
				</c:if>
				RFP Release Details</a>
			</li>
		</ul>
	</c:if>
	<%--RFPDetails links ends--%>
	
	<%--ProposalsandEvaluations links start--%>
	<c:if test="${ProposalsandEvaluations ne null and (ProposalsandEvaluations.tabState eq 'E' or ProposalsandEvaluations.tabState eq 'D')}">
	    <ul class="roundcorners">
	        <li class="<c:if test="${ProposalsandEvaluations.selected eq true}">firstcurrent</c:if> <c:if test="${ProposalsandEvaluations.tabState eq 'D'}">tabDisabled</c:if> nobdr" id="ProposalsandEvaluations">
	        	<a class="navigationR2Class" href="#" >
	        	<c:if test="${ProposalsandEvaluations.tabState ne 'D'}">
					<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProposalsandEvaluations" />
					<input type="hidden" name="forAction" id="forAction" value="propEval" />
				</c:if>
				Proposals and Evaluations</a>
	        </li>
	    </ul>
    </c:if>
    <%--ProposalsandEvaluations links ends--%>
    <%--AwardsandContracts links start--%>
    <c:if test="${AwardsandContracts ne null and (AwardsandContracts.tabState eq 'E' or AwardsandContracts.tabState eq 'D')}">
		<ul class="roundcorners">
			<li class="<c:if test="${AwardsandContracts.selected eq true}">firstcurrent</c:if> <c:if test="${AwardsandContracts.tabState eq 'D'}">tabDisabled</c:if> nobdr" id="AwardsandContracts">
				<a class="navigationR2Class" href="#" >
				<c:if test="${AwardsandContracts.tabState ne 'D'}">
					<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="AwardsandContracts" />
					<input type="hidden" name="render_action" id="render_action" value="awardsAndContracts" />
					<input type="hidden" name="forAction" id="forAction" value="awardContract" />
				</c:if>
				Awards and Contracts</a>
			</li>
		</ul>
	</c:if>
	<%--AwardsandContracts links ends--%>
	<%--City/Agency links ends--%>
	
	<%--Provider links start--%>
	<%--ProcurementSummaryHeader links start--%>
	<c:if test="${ProcurementSummaryHeader ne null and (ProcurementSummaryHeader.tabState eq 'E' or ProcurementSummaryHeader.tabState eq 'D')}">
	    <ul class="roundcorners">
	        <li class="<c:if test="${ProcurementSummaryHeader.selected eq true}">firstcurrent</c:if> <c:if test="${ProcurementSummaryHeader.tabState eq 'D'}">tabDisabled</c:if> nobdr" id="ProcurementSummaryHeader">
	        	<a class="navigationR2Class" href="#" >
	        	<c:if test="${ProcurementSummaryHeader.tabState ne 'D'}">
					<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementSummaryHeader" />
					<input type="hidden" name="render_action" id="render_action" value="procurementDetails" />
				</c:if>
				Procurement Summary</a>
	        </li>
	    </ul>
    </c:if>
    <%--ProcurementSummaryHeader links ends--%>
    <%--ServicesAndProviders links start--%>
    <c:if test="${ServicesAndProviders ne null and (ServicesAndProviders.tabState eq 'E' or ServicesAndProviders.tabState eq 'D')}">
	    <ul class="roundcorners">
	        <li class="<c:if test="${ServicesAndProviders.selected eq true}">firstcurrent</c:if> <c:if test="${ServicesAndProviders.tabState eq 'D'}">tabDisabled</c:if> nobdr" id="ServicesAndProviders">
	        	<a class="navigationR2Class" href="#" >
	        	<c:if test="${ServicesAndProviders.tabState ne 'D'}">
					<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ServicesAndProviders" />
					<input type="hidden" name="render_action" id="render_action" value="renderServicesAndProviderInfo" />
				</c:if>
				Services and Providers</a>
	        </li>
	    </ul>
    </c:if>
    <%--ServicesAndProviders links ends--%>
    <%--RFPDocumentsHeader links start--%>
    <c:if test="${RFPDocumentsHeader ne null and (RFPDocumentsHeader.tabState eq 'E' or RFPDocumentsHeader.tabState eq 'D')}">
	    <ul class="roundcorners">
	        <li class="<c:if test="${RFPDocumentsHeader.selected eq true}">firstcurrent</c:if> <c:if test="${RFPDocumentsHeader.tabState eq 'D'}">tabDisabled</c:if> nobdr" id="RFPDocumentsHeader">
	        	<a class="navigationR2Class" href="#" >
	        	<c:if test="${RFPDocumentsHeader.tabState ne 'D'}">
					<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="RFPDocumentsHeader" />
					<input type="hidden" name="render_action" id="render_action" value="displayRFPDocumentList" />
					<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
				</c:if>
				RFP Documents</a>
	        </li>
	    </ul>
    </c:if>
    <%--RFPDocumentsHeader links ends--%>
    <%--ProposalSummary links start--%>
    <%-- Start : Changes in R5 --%>
    <c:if test="${permissionType eq 'P' or permissionType eq 'FP'}">
    <c:if test="${ProposalSummary ne null and (ProposalSummary.tabState eq 'E' or ProposalSummary.tabState eq 'D')}">
	    <ul class="roundcorners">
	        <li class="<c:if test="${ProposalSummary.selected eq true}">firstcurrent</c:if> <c:if test="${ProposalSummary.tabState eq 'D'}">tabDisabled</c:if> nobdr" id="ProposalSummary">
	        	<a id='ProposalSummaryAnchor' class="navigationR2Class" href="#" >
	        	<c:if test="${ProposalSummary.tabState ne 'D'}">
					<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProposalSummary" />
					<input type="hidden" name="render_action" id="render_action" value="proposalSummary" />
					<input type="hidden" name="forAction" id="forAction" value="propEval" />
				</c:if>
				Proposal Summary</a>
	        </li>
	    </ul>
    </c:if>
    </c:if>
    <%--ProposalSummary links ends--%>
    <%--SelectionDetails links starts--%>
    <c:if test="${permissionType eq 'P' or permissionType eq 'FP'}">
    <c:if test="${SelectionDetails ne null and (SelectionDetails.tabState eq 'E' or SelectionDetails.tabState eq 'D')}">
	    <ul class="roundcorners">
	        <li class="<c:if test="${SelectionDetails.selected eq true}">firstcurrent</c:if> <c:if test="${SelectionDetails.tabState eq 'D'}">tabDisabled</c:if> nobdr" id="SelectionDetails">
	        	<a class="navigationR2Class" href="#" >
	        	<c:if test="${SelectionDetails.tabState ne 'D'}">
					<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="SelectionDetails" />
					<input type="hidden" name="render_action" id="render_action" value="viewSelectionDetails" />
					<input type="hidden" name="forAction" id="forAction" value="selectionDetail" />
				</c:if>
				Selection Details</a>
	        </li>
	    </ul>
	 </c:if>
	 </c:if>
	 <%-- End : Changes in R5 --%>
	 <%--SelectionDetails links ends--%>
	 <%--Provider links ends--%>
</div>
<%-- Application Main Navigation Section Ends --%>

<input type="hidden" value="${topLevelFromRequest}" id="topLevelFromRequest" name="topLevelFromRequest"/>
<input type="hidden" value="${midLevelFromRequest}" id="midLevelFromRequest" name="midLevelFromRequest"/>

<%--Second level Navigation starts--%>
<p class='clear'></p>

		<%-- City second level starts here	--%>
		<%--ProcurementInformation links starts--%>
		<c:if test="${ProcurementInformation ne null and (ProcurementInformation.tabState eq 'E' and ProcurementInformation.selected eq true)}">
			<div class='customtabs customtabsR2 tabspacing'>
				<ul>
					<c:if test="${ProcurementSummary ne null and (ProcurementSummary.tabState eq 'E' or ProcurementSummary.tabState eq 'D')}">
						<li class="<c:if test="${ProcurementSummary.selected eq true}">selected</c:if> <c:if test="${ProcurementSummary.tabState eq 'D'}">tabDisabled</c:if> "id="ProcurementSummary">
							<a class="navigationR2Class" href="#" >
							<c:if test="${ProcurementSummary.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementInformation" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ProcurementSummary" />
								<input type="hidden" name="render_action" id="render_action" value="viewProcurement" />
							</c:if>
							Procurement Summary</a>
						</li>
					</c:if>
					<%--ProcurementInformation links ends--%>
					<%--ApprovedProviders links starts--%>
					<c:if test="${ApprovedProviders ne null and (ApprovedProviders.tabState eq 'E' or ApprovedProviders.tabState eq 'D')}">
						<li class="<c:if test="${ApprovedProviders.selected eq true}">selected</c:if> <c:if test="${ApprovedProviders.tabState eq 'D'}">tabDisabled</c:if> "id="ApprovedProviders">
							<a class="navigationR2Class" href="#" >
							<c:if test="${ApprovedProviders.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementInformation" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ApprovedProviders" />
								<input type="hidden" name="render_action" id="render_action" value="approvedproviders" />
							</c:if>
							Approved Providers</a>
						</li>
					</c:if>
					<%--ApprovedProviders links ends--%>
					<%--Financials links starts--%>
					<c:if test="${Financials ne null and (Financials.tabState eq 'E' or Financials.tabState eq 'D')}">
						<li class="<c:if test="${Financials.selected eq true}">selected</c:if> <c:if test="${Financials.tabState eq 'D'}">tabDisabled</c:if> "id="Financials">
							<a class="navigationR2Class" href="#" >
							<c:if test="${Financials.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementInformation" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="Financials" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>
							Financials</a>
						</li>
					</c:if>
					<%--Financials links ends--%>
					<%--ProposalConfiguration links starts--%>
					<c:if test="${ProposalConfiguration ne null and (ProposalConfiguration.tabState eq 'E' or ProposalConfiguration.tabState eq 'D')}">
				        <li class="<c:if test="${ProposalConfiguration.selected eq true}">selected</c:if> <c:if test="${ProposalConfiguration.tabState eq 'D'}">tabDisabled</c:if> "id="ProposalConfiguration">
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${ProposalConfiguration.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementInformation" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ProposalConfiguration" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>
							Proposal Config</a>
				        </li>
					</c:if>
					<%--ProposalConfiguration links ends--%>
					<%-- Code updated for R4 Starts --%>
					<%--CompetitionConfiguration links starts--%>
					<c:if test="${CompetitionConfiguration ne null and (CompetitionConfiguration.tabState eq 'E' or CompetitionConfiguration.tabState eq 'D')}">
				        <li class="<c:if test="${CompetitionConfiguration.selected eq true}">selected</c:if> <c:if test="${CompetitionConfiguration.tabState eq 'D'}">tabDisabled</c:if> "id="CompetitionConfiguration">
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${CompetitionConfiguration.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementInformation" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="CompetitionConfiguration" />
								<input type="hidden" name="render_action" id="render_action" value="displayCompetitionConfiguration" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>
							Competition Config</a>
				        </li>
					</c:if>
					<%--CompetitionConfiguration links ends--%>
					<%-- Code updated for R4 Ends --%>
					<%--RFPDocuments links starts--%>
					<c:if test="${RFPDocuments ne null and (RFPDocuments.tabState eq 'E' or RFPDocuments.tabState eq 'D')}">
				        <li class="<c:if test="${RFPDocuments.selected eq true}">selected</c:if> <c:if test="${RFPDocuments.tabState eq 'D'}">tabDisabled</c:if> "id="RFPDocuments">
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${RFPDocuments.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementInformation" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="RFPDocuments" />
								<input type="hidden" name="render_action" id="render_action" value="displayRFPDocumentList" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>
				        	RFP Documents</a>
				        </li>
					</c:if>
					<%--RFPDocuments links ends--%>
					<%--EvaluationCriteria links starts--%>
					<c:if test="${EvaluationCriteria ne null and (EvaluationCriteria.tabState eq 'E' or EvaluationCriteria.tabState eq 'D')}">
				        <li class="<c:if test="${EvaluationCriteria.selected eq true}">selected</c:if> <c:if test="${EvaluationCriteria.tabState eq 'D'}">tabDisabled</c:if> "id="EvaluationCriteria">
				       		<a class="navigationR2Class" href="#" >
				       		<c:if test="${EvaluationCriteria.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementInformation" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="EvaluationCriteria" />
								<input type="hidden" name="render_action" id="render_action" value="evaluationCriteria" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>Evaluation Criteria</a>
				       	</li>
					</c:if>
					<%--EvaluationCriteria links ends--%>
					<%--ReleaseAddendum links starts--%>
					<c:if test="${ReleaseAddendum ne null and (ReleaseAddendum.tabState eq 'E' or ReleaseAddendum.tabState eq 'D')}">		       
				        <li class="<c:if test="${ReleaseAddendum.selected eq true}">selected</c:if> <c:if test="${ReleaseAddendum.tabState eq 'D'}">tabDisabled</c:if> "id="ReleaseAddendum">
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${ReleaseAddendum.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementInformation" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ReleaseAddendum" />
								<input type="hidden" name="render_action" id="render_action" value="renderReleaseAddendum" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>
				        	Release Addendum</a>
				        </li>
					</c:if>
					<%--ReleaseAddendum links ends--%>
					<%--PublishProcurement links starts--%>
					<c:if test="${PublishProcurement ne null and (PublishProcurement.tabState eq 'E' or PublishProcurement.tabState eq 'D')}">
				        <li class="<c:if test="${PublishProcurement.selected eq true}">selected</c:if> <c:if test="${PublishProcurement.tabState eq 'D'}">tabDisabled</c:if> "id="PublishProcurement">
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${PublishProcurement.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementRoadmapDetails" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="PublishProcurement" />
								<input type="hidden" name="render_action" id="render_action" value="renderpublishProcurement" />
							</c:if>
				        	Publish Procurement</a>
				        </li>
					</c:if>
					<%--PublishProcurement links ends--%>
				</ul>
			</div>
		</c:if>
		<%-- ProcurementRoadmapDetails link starts	--%>
		<c:if test="${ProcurementRoadmapDetails ne null and (ProcurementRoadmapDetails.tabState eq 'E' and ProcurementRoadmapDetails.selected eq true)}">
			<div class='customtabs customtabsR2'>
				<ul>
					<%-- ProcurementSummary link starts	--%>
					<c:if test="${ProcurementSummary ne null and (ProcurementSummary.tabState eq 'E' or ProcurementSummary.tabState eq 'D')}">
						<li class="<c:if test="${ProcurementSummary.selected eq true}">selected</c:if> <c:if test="${ProcurementSummary.tabState eq 'D'}">tabDisabled</c:if> "id="ProcurementSummary">
							<a class="navigationR2Class" href="#" >
							<c:if test="${ProcurementSummary.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementRoadmapDetails" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ProcurementSummary" />
								<input type="hidden" name="render_action" id="render_action" value="viewProcurement" />
							</c:if>
							Procurement Summary</a>
						</li>
					</c:if>
					<%-- ProcurementSummary link ends	--%>
					<%-- ServiceSelection link starts	--%>
					<c:if test="${ServiceSelection ne null and (ServiceSelection.tabState eq 'E' or ServiceSelection.tabState eq 'D')}">
						<li class="<c:if test="${ServiceSelection.selected eq true}">selected</c:if> <c:if test="${ServiceSelection.tabState eq 'D'}">tabDisabled</c:if> "id="ServiceSelection">
							<a class="navigationR2Class" href="#" >
							<c:if test="${ServiceSelection.tabState ne 'D' and procurementId ne null}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementRoadmapDetails" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ServiceSelection" />
								<input type="hidden" name="render_action" id="render_action" value="serviceSelectionRender" />
							</c:if>
							Service Selection</a>
						</li>
					</c:if>
					<%-- ServiceSelection link ends	--%>
					<%-- ApprovedProviders link starts	--%>
					<c:if test="${ApprovedProviders ne null and (ApprovedProviders.tabState eq 'E' or ApprovedProviders.tabState eq 'D')}">
				        <li class="<c:if test="${ApprovedProviders.selected eq true}">selected</c:if> <c:if test="${ApprovedProviders.tabState eq 'D'}">tabDisabled</c:if> "id="ApprovedProviders">
				       		<a class="navigationR2Class" href="#" >
				       		<c:if test="${ApprovedProviders.tabState ne 'D' and procurementId ne null}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementRoadmapDetails" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ApprovedProviders" />
								<input type="hidden" name="render_action" id="render_action" value="approvedproviders" />
							</c:if>Approved Providers</a>
				        </li>
					</c:if>
					<%-- ApprovedProviders link ends--%>
					<%-- PublishProcurement link starts	--%>
					<c:if test="${PublishProcurement ne null and (PublishProcurement.tabState eq 'E' or PublishProcurement.tabState eq 'D')}">
				        <li class="<c:if test="${PublishProcurement.selected eq true}">selected</c:if> <c:if test="${PublishProcurement.tabState eq 'D'}">tabDisabled</c:if> "id="PublishProcurement">
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${PublishProcurement.tabState ne 'D' and procurementId ne null}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProcurementRoadmapDetails" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="PublishProcurement" />
								<input type="hidden" name="render_action" id="render_action" value="renderpublishProcurement" />
							</c:if>
							Publish Procurement</a>
				        </li>
					</c:if>
					<%-- PublishProcurement link ends	--%>
				</ul>
			</div>
		</c:if>
		<%-- ProcurementRoadmapDetails link ends--%>
		<%-- RFPDetails link starts	--%>
		<c:if test="${RFPDetails ne null and (RFPDetails.tabState eq 'E' and RFPDetails.selected eq true)}">
			<div class='customtabs customtabsR2'>
				<ul>
					<%-- Financials link starts--%>
					<c:if test="${Financials ne null and (Financials.tabState eq 'E' or Financials.tabState eq 'D')}">
						<li class="<c:if test="${Financials.selected eq true}">selected</c:if> <c:if test="${Financials.tabState eq 'D'}">tabDisabled</c:if> "id="Financials">
							<a class="navigationR2Class" href="#" >
							<c:if test="${Financials.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="RFPDetails" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="Financials" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>
							Financials</a>
						</li>
					</c:if>
					<%-- Financials link ends--%>
					<%-- ProposalConfiguration link starts--%>
					<c:if test="${ProposalConfiguration ne null and (ProposalConfiguration.tabState eq 'E' or ProposalConfiguration.tabState eq 'D')}">
				        <li class="<c:if test="${ProposalConfiguration.selected eq true}">selected</c:if> <c:if test="${ProposalConfiguration.tabState eq 'D'}">tabDisabled</c:if> "id="ProposalConfiguration">
				        	<a class="navigationR2Class" href="#" >
							<c:if test="${ProposalConfiguration.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="RFPDetails" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ProposalConfiguration" />
								<input type="hidden" name="render_action" id="render_action" value="displayProposalConfiguration" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>
							Proposal Configuration</a>
				        </li>
			        </c:if>
			        <%-- ProposalConfiguration link ends--%>
			      <%-- Code updated for R4 Starts --%>
			        <%--CompetitionConfiguration links starts--%>
					<c:if test="${CompetitionConfiguration ne null and (CompetitionConfiguration.tabState eq 'E' or CompetitionConfiguration.tabState eq 'D')}">
				        <li class="<c:if test="${CompetitionConfiguration.selected eq true}">selected</c:if> <c:if test="${CompetitionConfiguration.tabState eq 'D'}">tabDisabled</c:if> "id="CompetitionConfiguration">
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${CompetitionConfiguration.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="RFPDetails" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="CompetitionConfiguration" />
								<input type="hidden" name="render_action" id="render_action" value="displayCompetitionConfiguration" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>
							Competition Configuration</a>
				        </li>
					</c:if>
					<%--CompetitionConfiguration links ends--%>
			        <%-- Code updated for R4 Ends --%>
			        <%-- RFPDocuments link starts--%>
					<c:if test="${RFPDocuments ne null and (RFPDocuments.tabState eq 'E' or RFPDocuments.tabState eq 'D')}">
				        <li class="<c:if test="${RFPDocuments.selected eq true}">selected</c:if> <c:if test="${RFPDocuments.tabState eq 'D'}">tabDisabled</c:if> "id="RFPDocuments">
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${RFPDocuments.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="RFPDetails" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="RFPDocuments" />
								<input type="hidden" name="render_action" id="render_action" value="displayRFPDocumentList" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>
							RFP Documents</a>
				        </li>
			        </c:if>
			        <%-- RFPDocuments link ends--%>
			        <%-- EvaluationCriteria link starts--%>
					<c:if test="${EvaluationCriteria ne null and (EvaluationCriteria.tabState eq 'E' or EvaluationCriteria.tabState eq 'D')}">
				        <li class="<c:if test="${EvaluationCriteria.selected eq true}">selected</c:if> <c:if test="${EvaluationCriteria.tabState eq 'D'}">tabDisabled</c:if> "id="EvaluationCriteria">
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${EvaluationCriteria.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="RFPDetails" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="EvaluationCriteria" />
								<input type="hidden" name="render_action" id="render_action" value="evaluationCriteria" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>
				        	Evaluation Criteria</a>
				        </li>
			        </c:if>
			        <%-- EvaluationCriteria link ends--%>
			        <%-- ReleaseRFP link starts--%>
					<c:if test="${ReleaseRFP ne null and (ReleaseRFP.tabState eq 'E' or ReleaseRFP.tabState eq 'D')}">
				        <li class="<c:if test="${ReleaseRFP.selected eq true}">selected</c:if> <c:if test="${ReleaseRFP.tabState eq 'D'}">tabDisabled</c:if> "id="ReleaseRFP">
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${ReleaseRFP.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="RFPDetails" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ReleaseRFP" />
								<input type="hidden" name="render_action" id="render_action" value="renderReleaseRfp" />
								<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
							</c:if>
				        	Release RFP</a>
						</li>
			        </c:if>
			        <%-- ReleaseRFP link ends--%>
	        	</ul>
			</div>
        </c:if>
        <%-- RFPDetails link ends	--%>
        <%-- ProposalsandEvaluations link starts	--%>
		<c:if test="${ProposalsandEvaluations ne null and (ProposalsandEvaluations.tabState eq 'E' and ProposalsandEvaluations.selected eq true) and selectedChildTab ne 'EvaluationSummary' and selectedChildTab ne 'EvaluationGroupsSummary'}">
			<div class='customtabs customtabsR2'>
				<ul>
					<%-- EvaluationSettings link starts--%>
					<!--start QC 9069 R7.2 Disable Evaluation Settings tab for RFPs in selections made, closed and cancelled statuses for read only users --> 
					<%if( !CommonUtil.hideForOversightRole(request.getSession()) ){%>  
					<c:if test="${EvaluationSettings ne null and (EvaluationSettings.tabState eq 'E' or EvaluationSettings.tabState eq 'D')}">
					
						<li class="<c:if test="${EvaluationSettings.selected eq true}">selected</c:if> <c:if test="${EvaluationSettings.tabState eq 'D'}">tabDisabled</c:if> "id="EvaluationSettings">
							<a class="navigationR2Class" href="#" >
							<c:if test="${EvaluationSettings.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProposalsandEvaluations" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="EvaluationSettings" />
								<input type="hidden" name="forAction" id="forAction" value="propEval" />
								<input type="hidden" name="ES" id="ES" value="0" />
							</c:if>
							Evaluation Settings</a>
						</li>
			
			        </c:if>
			        <%}%> 
			        <%-- EvaluationSettings link ends--%>
			        <!--end QC 9069 R7.2 Disable Evaluation Settings tab for RFPs in selections made, closed and cancelled statuses for read only users --> 	
			        
			        <%-- EvaluationStatus link starts--%>
					<c:if test="${EvaluationStatus ne null and (EvaluationStatus.tabState eq 'E' or EvaluationStatus.tabState eq 'D')}">
					
				        <li class="<c:if test="${EvaluationStatus.selected eq true }">selected</c:if> <c:if test="${EvaluationStatus.tabState eq 'D'}">tabDisabled</c:if> "id="EvaluationStatus">
				      
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${EvaluationStatus.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProposalsandEvaluations" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="EvaluationStatus" />
								<input type="hidden" name="render_action" id="render_action" value="getEvaluationStatus" />
								<input type="hidden" name="forAction" id="forAction" value="propEval" />
								<input type="hidden" name="ES" id="ES" value="0" />
							</c:if>
				        	Evaluation Status</a>
				        </li>
			        </c:if>
			        <%-- EvaluationStatus link ends--%>
			        <%-- EvaluationResultsandSelections link starts--%>
					<c:if test="${EvaluationResultsandSelections ne null and (EvaluationResultsandSelections.tabState eq 'E' or EvaluationResultsandSelections.tabState eq 'D')}">
				        <li class="<c:if test="${EvaluationResultsandSelections.selected eq true}">selected</c:if> <c:if test="${EvaluationResultsandSelections.tabState eq 'D'}">tabDisabled</c:if> "id="EvaluationResultsandSelections">
				        	<a class="navigationR2Class" href="#" >
				        	<c:if test="${EvaluationResultsandSelections.tabState ne 'D'}">
								<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProposalsandEvaluations" />
								<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="EvaluationResultsandSelections" />
								<input type="hidden" name="render_action" id="render_action" value="fetchEvaluationResults" />
								<input type="hidden" name="forAction" id="forAction" value="propEval" />
								<input type="hidden" name="ES" id="ES" value="0" />
							</c:if>
				        	Evaluation Results and Selections</a>
				        </li>
					</c:if>
					<%-- Code updated for R4 Starts --%>
					<c:if test="${fn:length(competitionPoolList) > 1}">
						<li class="floatRht">
							<select id="compPoolDropDown">
								<option value="-1">Select Competition Pool</option>
								<c:forEach var="entry" items="${competitionPoolList}">
									<option value="${entry['EVALUATION_POOL_MAPPING_ID']}">${entry['COMPETITION_POOL_TITLE']}</option>
					            </c:forEach>
							</select>
						</li>
					</c:if>
					<%-- Code updated for R4 Ends --%>
					<%-- EvaluationResultsandSelections link ends--%>
				</ul>
			</div>
        </c:if>
        <%-- ProposalsandEvaluations link ends--%>
        <%-- City second level ends here--%>
        
        <%-- provider second level starts here	--%>
        <c:if test="${ProposalSummary ne null and (ProposalSummary.tabState eq 'E' and ProposalSummary.selected eq true)}">
        	<c:if test="${(ProposalDetails ne null and ProposalDetails.selected eq true and (ProposalDetails.tabState eq 'E' or ProposalDetails.tabState eq 'D')) || 
        			(ProposalDocuments ne null and ProposalDocuments.selected eq true and (ProposalDocuments.tabState eq 'E' or ProposalDocuments.tabState eq 'D')) || 
        			(SubmitProposal ne null and SubmitProposal.selected eq true and (SubmitProposal.tabState eq 'E' or SubmitProposal.tabState eq 'D'))}">
        		<div class='customtabs customtabsR2'>
					<ul>
						<c:if test="${ProposalDetails ne null and (ProposalDetails.tabState eq 'E' or ProposalDetails.tabState eq 'D')}">
							<li class="<c:if test="${ProposalDetails.selected eq true}">selected</c:if> <c:if test="${ProposalDetails.tabState eq 'D'}">tabDisabled</c:if> "id="ProposalDetails">
								<a class="navigationR2Class" href="#" >
								<c:if test="${ProposalDetails.tabState ne 'D'}">
									<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProposalSummary" />
									<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ProposalDetails" />
									<input type="hidden" name="render_action" id="render_action" value="procurementProposalDetails" />
									<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
								</c:if>
								Proposal Details</a>
							</li>
				        </c:if>
						<c:if test="${ProposalDocuments ne null and (ProposalDocuments.tabState eq 'E' or ProposalDocuments.tabState eq 'D')}">
					        <li class="<c:if test="${ProposalDocuments.selected eq true}">selected</c:if> <c:if test="${ProposalDocuments.tabState eq 'D'}">tabDisabled</c:if> "id="ProposalDocuments">
					        	<a class="navigationR2Class" href="#" >
					        	<c:if test="${ProposalDocuments.tabState ne 'D'}">
									<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProposalSummary" />
									<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="ProposalDocuments" />
									<input type="hidden" name="render_action" id="render_action" value="procurementProposalDocumentList" />
									<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
								</c:if>
					        	Proposal Documents</a>
					        </li>
				        </c:if>
						<c:if test="${SubmitProposal ne null and (SubmitProposal.tabState eq 'E' or SubmitProposal.tabState eq 'D')}">
					        <li class="<c:if test="${SubmitProposal.selected eq true}">selected</c:if> <c:if test="${SubmitProposal.tabState eq 'D'}">tabDisabled</c:if> "id="SubmitProposal">
					        	<a class="navigationR2Class" href="#" >
					        	<c:if test="${SubmitProposal.tabState ne 'D'}">
									<input type="hidden" name="topLevelFromRequest" id="topLevelFromRequest" value="ProposalSummary" />
									<input type="hidden" name="midLevelFromRequest" id="midLevelFromRequest" value="SubmitProposal" />
									<input type="hidden" name="render_action" id="render_action" value="renderProviderProposal" />
									<input type="hidden" name="forAction" id="forAction" value="rfpRelease" />
								</c:if>
					        	Submit Proposal</a>
					        </li>
						</c:if>
						<li class="floatRht" style='border:none'>
							<div class="floatRht">
								<b>Proposal Status</b>: ${proposalStatus} 
							</div>
						</li>
					</ul>
				</div>
			</c:if>
        </c:if>
    <%-- provider second level ends here	--%>
<%--Second level Navigation ends--%>

<%-- Form submitted for navigation starts --%>
<portlet:actionURL var="navigationFormAction" escapeXml="false"></portlet:actionURL>
<form id="navigationForm" name="navigationForm" action="${navigationFormAction}" method ="post" >
	<div id="navigationDiv">
		<input type="hidden" id="forAction" name="forAction" value=""/>
		<input type="hidden" id="render_action" name="render_action" value=""/>
		<input type="hidden" id="topLevelFromRequest" name="topLevelFromRequest" value=""/>
		<input type="hidden" id="midLevelFromRequest" name="midLevelFromRequest" value=""/>
		<input type="hidden" id="submit_action" name="submit_action" value="navigationAction" />
		<%-- Code updated for R4 Starts --%>
		<input type="hidden" id="competitionPoolId" name="competitionPoolId" value="${competitionPoolId}" />
		<input type="hidden" id="evaluationGroupId" name="evaluationGroupId" value="${evaluationGroupId}" />
		<input type="hidden" id="evaluationPoolMappingId" name="evaluationPoolMappingId" value="${evaluationPoolMappingId}" />
		<input type="hidden" id="ES" name="ES" value="" />
		<%-- Code updated for R4 Ends --%>
		<input type="hidden" id="procurementId" name="procurementId" value="${procurementId}" />
		<input type="hidden" id="hideExitProcurement" name="hideExitProcurement" value="${hideExitProcurement}" />
		<input type="hidden" id="proposalId" name="proposalId" value="${proposalId}" />
	</div>
</form>
<%-- Form submitted for navigation ends --%>


<%-- Below div is used to launch help overlay content for all jsps --%>
<div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box-contact">
      <div class="content">
            <div id="newTabs">
                  <div id="contactDiv"></div>
            </div>
      </div>
</div>


<div id="greyedBackgroundR2"><img src='../framework/skins/hhsa/images/loadingBlue.gif'></div>