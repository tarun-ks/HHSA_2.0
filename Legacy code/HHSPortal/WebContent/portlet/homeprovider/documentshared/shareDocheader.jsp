<!--This is the header which is displayed when the list of provider that have Documents shared with Organization 
is selected in the drop down on the Home page-->
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.util.ApplicationSession" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant, org.apache.commons.lang.StringEscapeUtils"%>
<style>
	.formcontainer table{
		clear: both;
	    width:100%;
	}
	.formcontainer table .header{
		color: #5077AA;
	    font-size: 13px;
	    font-weight: bold;
	    line-height: 18px;
	    padding: 0 0 10px;
	}
	.formcontainer table .questions{
		margin: 0 8px 3px 0;
	    min-height: 20px;
	    padding: 4px 5px 5px 0;
	    text-align: right;
	    width: 49.5%;
	    border-bottom:3px solid #fff;
	    background: #f2f2f2;
	}
	.formcontainer table .answers{
		min-height: 20px;
	    padding: 4px 0 4px 6px;
	    text-align: left;
	    width: 48%;
	    word-break:break-all;
	}
	.tableHeaderPrint{
		 background-color: #E4E4E4;
	}
	.tableRowEvenPrint{
		background-color: #FFFFFF;
	}
	.tableRowOddPrint{
	 	background-color: #F2F2F2;
	}
	.content td{
		width: 300px;
		padding-left: 10px;
	}
	.content table{
		width: 80%;
	}
	.content table.documentTablePrint{
		width: 90%;
	}
	.content th{
		font-weight: bold;
		padding-left: 10px;
	}
	.noContent{
		border:1px solid grey;
		padding: 5px 0 5px 10px;
	}
	table{
		padding-bottom:20px;
	}
	.headingFont{
		font-wight:bold;
		font-size: 18px;
	}
	.sectionHeading{
		font-size: 16px;
	}
	td br{
		display: block;
	}
	.print-heading{
		background: none;
		font-size:17px;
		font-weight:bold;
		text-decoration:underline;
	}
	.headingText{
		border-bottom: 2px solid black;
	    margin-bottom: 3px;
	    padding-top: 7px;
	}
	.subheading{
		font-weight: bold;
	}
	h2{
		width: 85%;
	}
	.taskButtons {
		clear: both;
	}
</style>
<portlet:defineObjects />
<!-- Start: R5 Added -->
<portlet:actionURL var="navigateToFinancial" escapeXml="false">
	<portlet:param name="submit_action" value="viewFinancial"/>
	<portlet:param name="action" value="proposalDetails" />
</portlet:actionURL>
<input type = 'hidden' value='${navigateToFinancial}' id='navigateToFinancialURL'/>
<!-- End: R5 Added -->					
<%
	String lsFilePath = "";
	if(renderRequest.getAttribute("fileToInclude") != null){
		lsFilePath = (String) renderRequest.getAttribute("fileToInclude");
	}
 %>
 <script type="text/javascript">
 $(document).ready(function (){
	 if('${org_type}' == 'provider_org' && '${param.action}' == 'OrgInformation'){
	 $("#providerSharedOrgId").change(function (){
			document.providerInfo.action = document.providerInfo.action +"&providerId="+$(this).val();
			document.providerInfo.submit();
		});
	 }
       if('${org_type}' == 'agency_org' || '${org_type}' == 'city_org')
		{
		var len = 100/$('#listCityAppSummary ul li').length - 0.11;
		$('#listCityAppSummary ul li').css('width',len+'%'); 
		$('#listCityAppSummary ul li').eq(4).css({'width':((100/$('#listCityAppSummary ul li').length)-1)+'%', "border":"none"}); 
		}
       else{
    	   $('#listCityAppSummary ul').css('width','auto');
       }
		//Start : R5 Added
		$("#financial").click(function (){
			var _providerName = $('#providerNameOnCitySelect').val();
			if(_providerName.length == 0)
			{
				_providerName = $('#providerNameOnAgencySelect').val();
			}
			document.providerInfo.action = $('#navigateToFinancialURL').val()+'&ProviderName='+_providerName;
			document.providerInfo.submit();
		});
		//End : R5 Added
		
		if($("#cityApplicationSummary").val()){
			$("#subsection_questions11").addClass("current");
		}
		if('${param.action}' ==  'documentVault' || '${action}' ==  'documentVault'){
			$("#section_sharedDoc").addClass("current");
		}
		//Start : R5 Added
		else if('${param.action}' == 'proposalDetails'){
			$("#subsection_proposal").addClass("current");
		}
		//End : R5 Added
		/* $('#subsection_${subsection}').parent().removeClass(); */
		$('#subsection_${subsection}').addClass('selected');
		$(".current").attr("href", "#");
		$(".current").parent().css('background-color','#26b83f');
		$(".active").attr("href", "#");
		$(".selected").attr("href", "#");
	});
	
	$(function(){
		$(".iconQuestion").click(function(e){
			var actionA = '${action}';
			if(actionA.length == 0){
				actionA = '${param.action}';
			}
			if(actionA == 'businessSummary'){
				pageSpecificHelp('Applications');
			}else if(actionA ==  'documentVault'){
				pageSpecificHelp('Document Vault');
			}else{
				pageSpecificHelp('Organization Information');
			}
		});
	});
</script>

<!-- Start: R5 Added -->
<input type="hidden" id="providerNameOnAgencySelect" value="${portletSessionScope.providerNameForSharedDoc}" >
<input type="hidden" id="providerNameOnCitySelect" value='<%=StringEscapeUtils.unescapeJavaScript((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME_CITY))%>' >
<!-- End: R5 Added -->
<input type="hidden" name="documentOriginator" value="${documentOriginator}" >
<input type="hidden" name="cityUserSearchProviderId" value="${cityUserSearchProviderId}" >
<c:set var="headerNameForHelpOverlay" value="Organization Information" scope="application"/>
<c:if test="${action eq 'businessSummary'}">
	<c:set var="headerNameForHelpOverlay" value="Application" scope="application"/>
</c:if>
<c:if test="${action eq 'documentVault' || param.action eq 'documentVault' }">
	<c:set var="headerNameForHelpOverlay" value="Document Vault" scope="application"/>
</c:if>
<c:set var="cityUserProviderSearchId" value="${searchProviderId}" scope="application"/>
<input type="hidden" id="cityApplicationSummary" value="${cityApplicationSummary}"/>
<c:choose>
	<c:when test="${org_type eq 'city_org'}">
		<h2><%=StringEscapeUtils.unescapeJavaScript((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME_CITY))%></h2>
		<div class="linkReturnVault">
			<a title='Exit Application' href="<portlet:actionURL ><portlet:param name="next_action" value="returnToHome" /></portlet:actionURL>&app_menu_name=home_icon">Exit Application</a>
		</div>
	</c:when>
	<c:otherwise>
	
		<h2><%=StringEscapeUtils.unescapeJavaScript((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME_CITY))%></h2>
		<c:choose>
		<c:when test="${portletSessionScope.fromNotification eq null }">
			<div class="linkReturnVault">
				<a title='Return to Home' href="<portlet:actionURL ><portlet:param name="next_action" value="returnToHome" /></portlet:actionURL>&app_menu_name=home_icon">Return to Home</a>
			</div>
		</c:when>
		<c:otherwise>
		<c:choose>
			<c:when test="${org_type eq 'agency_org'}">
				<div class="linkReturnVault">
					<a title='Return to Home' href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agency_home&_nfls=false&app_menu_name=home_icon">Return to Home</a>
				</div>
			</c:when>
			<c:otherwise>
				<div class="linkReturnVault">
					<a title='Return to Home' href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_provider_home&_nfls=false&app_menu_name=home_icon">Return to Home</a>
				</div>
			</c:otherwise>
		</c:choose>
		</c:otherwise>
		
		</c:choose>
	</c:otherwise>
</c:choose>

<c:if test="${org_type != 'city_org' || docOriginator eq 'provider_org'}">
	<div class='clear'>&nbsp;</div>
</c:if>

<c:if test="${docOriginator eq 'provider_org' ||  empty docOriginator}">

<div class="appnavbar" id='listCityAppSummary' style='width:100%;'>
	<ul class='roundcorners' id="subheadercity" style='width:100%;'>
		<li class='appnavbar-align leftradius'><a id="section_basics" title="View basic organization information about this provider" href="<portlet:renderURL><portlet:param name="action" value="OrgInformation" /><portlet:param name="section" value="basics" /><portlet:param name="subsection" value="questions"/><portlet:param name="cityUserSearchProviderId" value="${ownerProviderId}"/><portlet:param name="headerJSPName" value="shareDocheader" /><portlet:param name="next_action" value="showquestion" /><portlet:param name="needPrintableView" value="true"/></portlet:renderURL>">Organization Information</a></li>
		<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())){%>
	<input type="hidden" id="providerNametoCheck" value='<%=StringEscapeUtils.unescapeJavaScript((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE))%>' >
	<input type="hidden" value="${org_type}" id="docOriginator"/>
	<input type="hidden" value="${fn:containsIgnoreCase(cityUserSearchProviderId,'provider_org')}" id="TestdocOriginator"/>
		<c:choose>			
			<c:when test="${org_type eq 'city_org'}">
				<li class='appnavbar-align'><a id="section_sharedDoc" title="View the documents this provider has uploaded to their document vault" href="<portlet:renderURL><portlet:param name="action" value="documentVault" /><portlet:param name="section" value="sharedDoc" /><portlet:param name="subSection" value="documentlist"/><portlet:param name="next_action" value="openProviderView" /><portlet:param name="provider" value="${ownerProviderId }"/><portlet:param name="providerName" value="${portletSessionScope.providerNameForSharedDoc }"/><portlet:param name="cityUserSearchProviderId" value="${cityUserSearchProviderId }"/><portlet:param name="headerClick" value="false"/><portlet:param name="documentOriginator" value="${docOriginator}"/></portlet:renderURL>">Document Vault</a></li>	
			</c:when>
			<c:otherwise>
			<!-- Start: R5 Added -->	
			<c:choose>
			<c:when test="${ org_type eq 'agency_org' and fn:containsIgnoreCase(cityUserSearchProviderId,'provider_org') }">
			<li class='appnavbar-align'><a id="section_sharedDoc" title="View the documents this provider has chosen to share with you" href="<portlet:renderURL><portlet:param name="action" value="documentVault"/><portlet:param name="headerClick" value="false"/><portlet:param name="section" value="sharedDoc" /><portlet:param name="subSection" value="documentlist"/><portlet:param name="next_action" value="open" /><portlet:param name="provider" value="${ownerProviderId }"/><portlet:param name="cityUserSearchProviderId" value="${cityUserSearchProviderId }"/><portlet:param name="documentOriginator" value="${docOriginator}"/></portlet:renderURL>">Document Vault</a></li>
			</c:when>
			<c:otherwise>
			<li class='appnavbar-align'><a id="section_sharedDoc" title="View the documents this provider has chosen to share with you" href="<portlet:renderURL><portlet:param name="action" value="documentVault"/><portlet:param name="headerClick" value="false"/><portlet:param name="section" value="sharedDoc" /><portlet:param name="subSection" value="documentlist"/><portlet:param name="next_action" value="open" /><portlet:param name="provider" value="${ownerProviderId }"/><portlet:param name="cityUserSearchProviderId" value="${cityUserSearchProviderId }"/><portlet:param name="documentOriginator" value="${docOriginator}"/></portlet:renderURL>">Shared Documents</a></li>
			</c:otherwise>
			</c:choose>
			<!-- End: R5 Added -->	
			</c:otherwise>
		</c:choose>
		<%} %>	
		<!-- Start: R5 Added -->			
		<c:if test="${org_type eq 'city_org' || org_type eq 'agency_org'}">
		<form method="post" action="<portlet:actionURL ><portlet:param name="action" value="providerAgencyHome" /></portlet:actionURL>" name="providerInfo"></form>
			<li class='appnavbar-align' id="shareDocBusinessSummary"><a id="subsection_questions11" href="<portlet:renderURL><portlet:param name="action" value="businessSummary"/><portlet:param name="headerJSPName" value="shareDocheader" /><portlet:param name="first_action" value="accelerator" /></portlet:renderURL>" title="View this provider's Applications">Application</a></li>
			<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.ACCO_AGENCY, request.getSession())) {%> 
			<li class='appnavbar-align' id="proposalList"><a id="subsection_proposal" title="View the Provider's proposal" href="<portlet:renderURL><portlet:param name="action" value="proposalDetails" /><portlet:param name="section" value="proposal" /><portlet:param name="subsection" value="show_proposal"/><portlet:param name="next_action" value="proposallist" /><portlet:param name="cityUserSearchProviderId" value="${cityUserSearchProviderId}"/></portlet:renderURL>">Proposals</a></li>
			<%}%>
			<li class='appnavbar-align'><a href="#" id="financial">Financials</a></li>
		<!-- End: R5 Added -->
		</c:if>
	</ul>
	<c:if test="${org_type eq 'provider_org' && param.action eq 'OrgInformation'}">
	 	<div class='floatRht' style="padding-top:10px;">
 			<select id = "providerSharedOrgId" name="providerSharedOrgId" class="input" >
				<option selected="selected">Switch to a different organization</option>
				<c:forEach var="organization" items="${portletSessionScope.sharedDocForProvider}">
					<option value="<c:out value="${organization.key}"/>"><c:out value="${organization.value}"/></option>
				</c:forEach>
			</select>
		</div>
		<form method="post" action="<portlet:actionURL ><portlet:param name="action" value="providerAgencyHome" /></portlet:actionURL>" name="providerInfo"></form>
	</c:if>
	</div>
</c:if>
	 
<div class='clear'>&nbsp;</div>

<c:if test="${!cityApplicationSummary}">
	<c:choose>
    	<c:when test="${section == 'basics' or section == 'filings'}">
		   <div class="customtabs customtabs-mo" >
		   	<c:choose>									<%--Start: R5 Added --%>	
    			<c:when test="${org_type == 'city_org' || org_type == 'agency_org'}">
		 			<ul class='customtabul-mo'>								<%--End: R5 Added --%>	
						<li class='subsection-align'><a id="subsection_questions" title="Basics" href="<portlet:renderURL><portlet:param name="action" value="OrgInformation" /><portlet:param name="cityUserSearchProviderId" value="${ownerProviderId }"/><portlet:param name="section" value="basics" /><portlet:param name="headerJSPName" value="shareDocheader" /><portlet:param name="subsection" value="questions" /><portlet:param name="next_action" value="showquestion" /><portlet:param name="needPrintableView" value="true" /></portlet:renderURL>">Basics</a></li>
						<li class='subsection-align'><a id="subsection_geography" title="Geography" href='<portlet:renderURL><portlet:param name="action" value="OrgInformation" /><portlet:param name="cityUserSearchProviderId" value="${ownerProviderId }"/><portlet:param name="section" value="basics" /><portlet:param name="headerJSPName" value="shareDocheader" /><portlet:param name="subsection" value="geography"/><portlet:param name="next_action" value="open" /></portlet:renderURL>'>Geography</a></li>
						<li class='subsection-align'><a id="subsection_languages" title="Languages" href='<portlet:renderURL><portlet:param name="action" value="OrgInformation" /><portlet:param name="cityUserSearchProviderId" value="${ownerProviderId }"/><portlet:param name="section" value="basics" /><portlet:param name="headerJSPName" value="shareDocheader" /><portlet:param name="subsection" value="languages"/><portlet:param name="next_action" value="open" /></portlet:renderURL>'>Languages</a></li>
						<li class='subsection-align'><a id="subsection_populations" title="Population" href="<portlet:renderURL><portlet:param name="action" value="OrgInformation" /><portlet:param name="cityUserSearchProviderId" value="${ownerProviderId }"/><portlet:param name="section" value="basics" /><portlet:param name="headerJSPName" value="shareDocheader" /><portlet:param name="subsection" value="populations"/><portlet:param name="next_action" value="open" /></portlet:renderURL>" >Population</a></li>
						<%-- Added in release 5 for module Proposal Activity History--%>
						<%--Start: R5 Added --%>
						<c:if test="${org_type == 'city_org'}">
							<li class='subsection-align' ><a id="subsection_filingsManageOrganization" title="Filings" href="<portlet:renderURL><portlet:param name="action" value="OrgInformation" /><portlet:param name="cityUserSearchProviderId" value="${ownerProviderId }"/><portlet:param name="section" value="filings" /><portlet:param name="headerJSPName" value="shareDocheader" /><portlet:param name="subsection" value="filingsManageOrganization"/><portlet:param name="next_action" value="open" /></portlet:renderURL>" >Filings</a></li>
						</c:if>
						<%--End: R5 Added --%>
						<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S101_SECTION, request.getSession()) ||
								// R5 Added ComponentRoleDisplay Condition
								CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())){%>
							<li class='subsection-align'><a id="subsection_memberandusers" title="Members & Users" href="<portlet:renderURL><portlet:param name="action" value="manageMembers" /><portlet:param name="section" value="basics" /><portlet:param name="cityUserSearchProviderId" value="${ownerProviderId }"/><portlet:param name="headerJSPName" value="shareDocheader" /><portlet:param name="subsection" value="memberandusers"/><portlet:param name="next_action" value="displayOrgMember" /></portlet:renderURL>" >Members & Users</a></li>
						<%} %>
					</ul>
				</c:when>
				<c:otherwise>
					<ul>	
						<li class='selected subtab-mo'><a id="subsection_questions11" href="#">Organization Basics</a></li>
					</ul>
					
				</c:otherwise>
			</c:choose>
	   </div>
		</c:when>
      	<c:otherwise>
		    <c:if test="${docOriginator eq 'provider_org'}">
	      		<c:choose>
		      		<c:when test="${section != 'basics' and org_type != 'city_org'}">
	    		    	<div class="customtabs">
							<ul>
								<li class='selected'><a id="subsection_questions11" href="#">Shared Documents</a></li>
							</ul>
						</div>
	    			</c:when>
	      		</c:choose>
	      	</c:if>
    	</c:otherwise>
	</c:choose>
</c:if>
<!-- Form Data Starts -->
<div class='formcontainer formcontainer-align'>
	 <c:choose>
	   <c:when test="${section == 'basics' and subsection == 'questions'}">
	   <div class='clear'></div>
	  	<h2>Organization Basics</h2>  
	   </c:when>
	   </c:choose>
 	<c:if test="${param.action ne 'proposalDetails'}">
 		<div class="iconQuestion"><a href="javascript:void(0);" ></a></div>
 	</c:if> 
	
	<c:choose>
		<c:when test="${printView eq null}">
	    	<jsp:include page="<%=lsFilePath%>">
	    	<jsp:param name="docOriginator" value="${docOriginator}"/>
	    	</jsp:include>
		</c:when>
	    <c:otherwise>
	    	<c:choose>
				<c:when test="${printView eq 'no'}">
			    	<jsp:include page="<%=lsFilePath%>"></jsp:include>
				</c:when>
			    <c:otherwise>
			    	${printView}
			    </c:otherwise>
			</c:choose>
	    </c:otherwise>
	</c:choose>
	<script type="text/javascript">
	/* Start : Updated in R5 */
	var _section = '${section}';
		if(_section == 'filings')
		{
			_section = "basics";
		}
		showSelectedOrg(_section,'${subsection }','header_organization_information1');
	/* End : Updated in R5 */
	</script>
</div>
<c:if test="${param.action ne 'proposalDetails'}">
	<div class='clear'>&nbsp;</div>
</c:if>
<div class="overlay"></div>
<div class="alert-box-help">
       <div class="tabularCustomHead">${headerNameForHelpOverlay } - Help Documents</div>
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
<c:set var="headerNameForHelpOverlay" value="" scope="application"/>