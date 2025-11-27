<%@ page errorPage="/error/errorpage.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ page import="com.nyc.hhs.util.HHSUtil"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:defineObjects/>

<%-- Script  & style--%>

<script type="text/javascript">
var totalNotEqual= "<fmt:message key='PROC_FIN_TOTAL_NOT_EQUAL'/>";
var procID = '${procurementId}';
var WFstatus = "${WFStatus}";
var status = "${ProcurementCOF.procurementStatus}";
var statusCOF ="${readOnlySection}";
</script>

<style>
	h2{width:78%}
	#myform table{
		border:0px;
	}
	.accContainer{
		width: 99.5% !important
	}
</style>

<%-- JQuery Grid links start--%>		
	<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/r2/css/ui.jqgrid.css" type="text/css"/>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/financials.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<%-- JQuery Grid links end--%>	
	<portlet:actionURL var="viewProcCOF" escapeXml="false">
	 	<portlet:param name="action" value="rfpRelease"/>
		<portlet:param name="submit_action" value="showProcCOF"/>
		<portlet:param name="procID" value="${ProcID}"/>
		<portlet:param name="ContractStartDate" value="${ProcurementCOF.origContractStartDate}"/>
		<portlet:param name="ContractEndDate" value="${ProcurementCOF.origContractEndDate}"/>
	</portlet:actionURL>
	
	<input type="hidden" name="viewProcCOF" id="viewProcCOF"  value="${viewProcCOF}"/>
	<portlet:actionURL var="launchWFproc" escapeXml="false">
		<portlet:param name="action" value="rfpRelease"/>
		<portlet:param name="submit_action" value="launchWFproc"/>
		<portlet:param name="ProcID" value="${procurementId}"/>
		<portlet:param name="agencyId" value="${ProcurementCOF.agencyId}"/>
 	</portlet:actionURL>
 	<input type="hidden" name="launchWFproc" id="launchWFproc"  value="${launchWFproc}"/>

<%-- Accounts grid attributes starts --%>
	<portlet:resourceURL var='mainAccountGrid' id='mainAccountGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsAccountGrid"/>
	</portlet:resourceURL>
	
	<portlet:resourceURL var='subAccountGrid' id='subAccountGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsAccountGrid"/>
	</portlet:resourceURL>	
	
	<portlet:resourceURL var='accountOperationGrid' id='accountOperationGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsAccountGrid"/>
	</portlet:resourceURL>
<%-- Accounts grid attributes ends --%>

<%-- Funds grid attributes starts --%>
	<portlet:resourceURL var='mainFundingGrid' id='mainFundingGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsFundingGrid"/>
	</portlet:resourceURL>
	
	<portlet:resourceURL var='subFundingGrid' id='subFundingGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsFundingGrid"/>
	</portlet:resourceURL>
	
	<portlet:resourceURL var='fundingOperationGrid' id='fundingOperationGrid' escapeXml='false'>
		<portlet:param name="screenName" value="financialsFundingGrid"/>
	</portlet:resourceURL>
<%-- Funds grid attributes ends --%>

<nav:navigationSM screenName="Financials">
<d:content section="<%=HHSComponentMappingConstant.S204_SCREEN%>" authorize="" >
	<input type="hidden" id="resourceURLHidden"	value="<portlet:resourceURL/>" />
		<portlet:actionURL var="addProcurementUrl" escapeXml="false">
			<portlet:param name="submitAction" value="addProcurement" />
		</portlet:actionURL>
	<form:form id="myform" name="myform" action="${addProcurementUrl}" method="post" commandName="ProcurementCOF">
	<div id=tabs-container>
			<c:if test="${accessScreenEnable eq false}">
				<div class="lockMessage" style="display:block">${lockedByUser} currently has edit access to this record. This record will be displayed in Read-only format.</div>
			</c:if>
			<H2>Financials</H2>
				<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
					<d:content section="${helpIconProvider}">
						<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
						<input type="hidden" id="screenName" value="Financials" name="screenName"/>
					</d:content>	
					
					<div class=hr></div>
					<c:if test="${message ne null}">
						<div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close" title="Close" alt="Close" onclick="showMe('messagediv', this)"></div>
					</c:if>
					
					<%-- Error and Passed Message Below --%>	
					<% String lsMessage = "";
						if(null != request.getAttribute("errorMessage")){
							lsMessage = (String) request.getAttribute("errorMessage");
						%>
							<div class="failed breakAll" style="display:block" id="error"><%=lsMessage%></div>			
					<%}else if(null != request.getAttribute("successMessage")){
						lsMessage = (String) request.getAttribute("successMessage");
						%>	
						<div class="passed breakAll" style="display:block" id="success"><%=lsMessage%></div>						
					<%}%> 
					<div class="failed" id="errorGlobalMsg"></div>
					<p>Please complete the Procurement Certification of Funds.</p>
					<div class='reqiredDiv'>
						<label class='required'>*</label>Indicates required fields
					</div>
						
					<%-- Form Data Starts --%>
					<div class='formcontainer'>
						<%-- Estimated Procurement Value in the PROCUREMENT TABLE --%>
					<div class='row'>
						<span class="label" title="The estimated value of the procurement for the length of the anticipated contract term">Procurement Value:</span> 
						<span class="formfield" id="procValue">
							$<label id="procurementValue">${ProcurementCOF.procurementValue}</label>
						</span>
					</div>
					<%-- Procurement updated start date in the PROCUREMENT TABLE --%>
					<div class='row'>
						<span class="label">Contract Start Date:</span> 
						<span class="formfield">
							${ProcurementCOF.contractStartDate}
						</span>
					</div>
					<%-- Procurement updated end date in the PROCUREMENT TABLE --%>
					<div class='row'>
						<span class="label">Contract End Date:</span> 
						<span class="formfield">
							${ProcurementCOF.contractEndDate}
						</span>
					</div>
					
					<%-- The default value is ‘Not Submitted and this value is set from the 
					status table when the Procurement is published to the Procurement Roadmap --%>
					<div class=row>
						<span class=label>Certification of Funds Status:</span> 
						<span class=formfield id="status">
							${ProcurementCOF.procurementStatus}
						</span> 							
						<%-- The button is visible & disabled by default. OnClick : opens S410 - Procurement Certification of Funds Document in a new window--%>	
					<INPUT class=button id="viewCoF" title="View CoF" value="View CoF" type=button onclick="OpenCOF();">
						</span>
					</div>
				</div>
				
		<P>&nbsp;</P>

		<div class='accContainer'>
		<d:content  isReadOnly="${readOnlySection ||(accessScreenEnable eq false)}" readonlyRoles="ACCO_STAFF,ACCO_ADMIN_STAFF,ACCO_MANAGER,PROGRAM_STAFF,PROGRAM_ADMIN_STAFF,PROGRAM_MANAGER,STAFF,MANAGER" readOnlyOrgType="agency_org, city_org">
			<H3>Chart of Accounts Allocation</H3>								
					<div class='gridFormField gridScroll'> 
						<jq:grid id="ProcCoAAllocation" 
						gridColNames="${GridColNames}"
						gridColProp="${MainHeaderProp}"
						subGridColProp="${SubHeaderProp}"
						gridUrl="${mainAccountGrid}" 
						positiveCurrency="${columnsForTotal}"
						modificationType="positive,positiveOnlyMsg"
						subGridUrl="${subAccountGrid}" cellUrl="${accountOperationGrid}"
		     			editUrl="${accountOperationGrid}" 
						dataType="json" methodType="POST" columnTotalName="${columnsForTotal}"
						nonEditColumnName="total" isCOAScreen="true" autoWidth="false"
						isPagination="true" rowsPerPage="5" isSubGrid="true" isReadOnly="false" notAllowDuplicateColumn="uobc,subOC,rc"
						operations="del:true,edit:true,add:true,cancel:true,save:true"
						 />	
					 </div>		 
					 	
		<P>&nbsp;</P>
			<H3>Funding Source Allocation (Optional)</H3>
				<div class='gridFormField gridScroll'> 
					<jq:grid id="fundingSource" 
						gridColNames="${FundingGridColNames}"
						gridColProp="${FundingMainHeaderProp}"
						subGridColProp="${FundingSubHeaderProp}"
						gridUrl="${mainFundingGrid}" 
						positiveCurrency="${columnsForTotal}" 
						nonEditColumnName="total" autoWidth="false" isCOAScreen="true"
						subGridUrl="${subFundingGrid}" cellUrl="${fundingOperationGrid}" editUrl="${fundingOperationGrid}"
						dataType="json" methodType="POST" columnTotalName="${columnsForTotal}"
						isPagination="true" rowsPerPage="5" isSubGrid="true" isReadOnly="false"
						operations="del:false,edit:true,add:false,cancel:true,save:true"
						 />
				</div>
		</d:content>
		</div>
		<%--Start of changes for release 3.2.0 enhancement 5684 : submit cof button removed from financial screen. Task is now generated by city user --%>
		
		<%--
					 The button will be visible & disabled 
				The below if condition is modified as per defect 5629 for release 2.6.0 
			<d:content  section="<%=HHSComponentMappingConstant.S204_SCREEN_SUBMIT_BUTTON%>" >	
				<c:if test="${accessScreenEnable ne false and ProcurementCOF.status ne 'Cancelled' and ProcurementCOF.status ne 'Closed'}">
					<div class=buttonholder>
						<input type='button' id="submitCoF" title='Submit CoF'  value='Submit CoF' class='button' onclick="submit1();"/>					
					</div>
				</c:if>
			</d:content>
		--%>
			
		<%--End of changes for release 3.2.0 enhancement 5684 --%>
		</div>
				<input type="hidden" id="topLevelFromRequest" name="topLevelFromRequest" value="${topLevelFromRequest}"/>
				<input type="hidden" id="midLevelFromRequest" name="midLevelFromRequest" value="${midLevelFromRequest}"/>
				
				
		<%-- Overlay Starts --%>
			<div class="overlay"></div>
		</form:form>
		</d:content>
</nav:navigationSM>