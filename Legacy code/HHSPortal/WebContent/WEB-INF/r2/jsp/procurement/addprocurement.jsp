<%--This jsp is used for add procurement (S203) screen--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib prefix="comSM" uri="/WEB-INF/tld/commonSolicitation.tld"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<portlet:defineObjects />
<%--8403 changes start --%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script> 
<%--Changes end --%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/addNewProcurement.js"></script>
<style>
h2{width:82%}
.individualErrorMsg, .error{
	color:#D63301;
}
.formcontainer .row span.error {
    color: #D63301;
    float: left;
    padding: 4px 0;
    text-align: left;
    
    padding:0;
	background:transparent;
	line-height:auto;
	 *font-size: 0px;
}
.formcontainer .row span.error label.error{
	 *font-size:12px;
}

</style>

<%--Start of changes for release 3.2.0 enhancement 5684 --%>
<script type="text/javascript">
var taskLaunch = "${TaskLaunch}";
<%-- Start : Changes in R5 --%>
var generatedPSRFlag = "${Procurement.generateTaskFlag}";
<%-- End : Changes in R5 --%>
var agencyID = "${Procurement.agencyId}";
</script>
<%--End of changes for release 3.2.0 enhancement 5684 --%>

<%--defining action url on click of save button from the page --%>
<portlet:actionURL var="addProcurementUrl" escapeXml="false">
	<portlet:param name="submit_action" value="addProcurement"/>
	<portlet:param name="saveAction" value="save"/>
	<portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
	<portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}"/>
</portlet:actionURL>

<%--Start of changes for release 3.2.0 enhancement 5684 : PCOF task is launched by city user --%>
<portlet:resourceURL var='launchPCOF' id='launchPCOF' escapeXml='false'>
</portlet:resourceURL>
<input type="hidden" name="launchPCOF" id="launchPCOF"  value="${launchPCOF}"/>
<%--End of changes for release 3.2.0 enhancement 5684 --%>

<%--defining render url --%>
<portlet:renderURL var="redirectURL" escapeXml="false">
	<portlet:param name="render_action" value="viewProcurement"/>
	<portlet:param name="procurementId" value="${procurementId}"/>
	<portlet:param name="topLevelFromRequest" value="ProcurementRoadmapDetails"/>
	<portlet:param name="midLevelFromRequest" value="ProcurementSummary"/>
</portlet:renderURL>
<%--defining resource url on click of close procurement button from the popup --%>
<portlet:resourceURL var='closeProcurementOverlay' id='closeProcurementOverlay' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${closeProcurementOverlay}' id='hiddencloseProcurementOverayContentUrl'/>
<portlet:resourceURL var='selectOverlayContent' id='selectOverlayContent' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${selectOverlayContent}' id='hiddencancelProcurementOverayContentUrl'/>
<%--defining resource urls--%>
<portlet:resourceURL var="resourceURLHiddenEPIN"></portlet:resourceURL>
<portlet:resourceURL var="getProgramListForAgency" id="getProgramListForAgency"></portlet:resourceURL>
<portlet:resourceURL var="getEpinListResourceUrl" id="getEpinListResourceUrl" escapeXml="false"></portlet:resourceURL>
<portlet:resourceURL var="getEpinDetailsResourceUrl" id="getEpinDetailsResourceUrl" escapeXml="false"></portlet:resourceURL>
<input type='hidden' id='getProgramListForAgency' value='${getProgramListForAgency}' />
<input type="hidden" id="resourceURLHidden" value="${resourceURLHiddenEPIN}" />
<input type = 'hidden' value='${getEpinListResourceUrl}' id='getEpinListResourceUrl'/>
<input type = 'hidden' value='${getEpinDetailsResourceUrl}' id='getEpinDetailsResourceUrl'/>
<input type = 'hidden' value='${redirectURL}' id='redirectURL'/>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<input type = 'hidden' value="${procurementStatus}" id="procurementStatusId"/>

<input type="hidden" id="closeProcurmentSuccess" value="${closeProcurmentSuccess}" />

<%--Navigation added on screen --%>
<nav:navigationSM screenName="ProcurementSummary">
<form:form id="addProcurementform" name="addProcurementform" action="${addProcurementUrl}" method ="post" commandName="Procurement">
	<input type="hidden" value="${procurementBean.status}" id="procurementStatus"/>
	<%-- Start : Changes in R5 --%>
	<input type="hidden" id="generateTaskFlag" name="generateTaskFlag" value="${Procurement.generateTaskFlag}"/>
	<input type="hidden" id="regeneratePDFFlag" name="regeneratePDFFlag" value="${Procurement.regeneratePDFFlag}"/>
	<%-- End : Changes in R5 --%>
	<div class='clear'></div>
	<%-- Form Data Starts --%>
	<div id='tabs-container'>
	<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}" readOnlyHref="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
	<h2>Procurement Summary 
	<%-- check for organization type --%>
	<input type='hidden' id='org_type' value='${org_type}' />
	<c:if test="${hideExitProcurement eq false}">
		<%-- check for procurement status --%>
		<c:if test="${readOnlySection eq null and (procurementId ne null and procurementId ne '' and contractRegistered ne 'true' and accessScreenEnable ne false)}">
			<c:set var="sectionCancelProcurement"><%=HHSComponentMappingConstant.S203_CANCEL_PROCUREMENT%></c:set>
			<d:content section="${sectionCancelProcurement}">
				<label class="cancel cancelProcurementPopup" title="Cancel Procurement" id="cancelProcurementLink">Cancel Procurement</label>
			</d:content>
		</c:if>
		<c:set var="sectionCloseProcurement"><%=HHSComponentMappingConstant.S203_CLOSE_PROCUREMENT%></c:set>
		<d:content section="${sectionCloseProcurement}">
			<c:if test="${readOnlySection eq null and Procurement.status eq '6' and accessScreenEnable ne false}">
				<label class="cancel cancelProcurementPopup" title="Close Procurement" id="closeProcurementLink">Close Procurement</label>
			</c:if>
		</d:content>
	</c:if>
	</h2>
	<c:set var="readOnlyValue" value=""></c:set>
		<c:if test="${readOnlySection ne null and readOnlySection ne ''}">
			<input type="hidden" id="screenReadOnly" value="true" name="screenReadOnly"/>
		</c:if>
	<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
		<d:content section="${helpIconProvider}">
			<div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" id="helpIconId" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
			<input type="hidden" id="screenName" value="Procurement Summary" name="screenName"/>
		</d:content>	
	<div class='hr'></div>
	
	<c:set var="sectionRFPReleaseDate"><%=HHSComponentMappingConstant.S203_RFP_RELEASE_DATE%></c:set>
	<d:content section="${sectionRFPReleaseDate}">	
		<c:if test="${unPublishedDataMsg ne null}">
			<div class='infoMessage' style="display:block">${unPublishedDataMsg}</div>
		</c:if>
	</d:content>
	<%-- check for fail or success message --%>
	<c:if test="${message ne null}">
		<div class="${messageType}" id="messagediv" style="display:block">${message} <img
			src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/iconClose.jpg" id="box"
			class="message-close" 
			onclick="showMe('messagediv', this)">
		</div>
	</c:if>
	<%--Start of changes for release 3.2.0 enhancement 5684: success div added for PCOF task message--%>
	<div id="successMessagePCOF" class="passed"> </div>
	<%--End of changes for release 3.2.0 enhancement 5684 --%>
	
	<%-- Code updated for R4 Starts --%>
	<div class="failed" id="messagediverrorjs" style="display:none">
		<div style="color:#d63301;"></div>
		<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/iconClose.jpg" id="box"
			class="message-close" 
			onclick="showMe('messagediverrorjs', this)">
	</div>
	<%-- Code updated for R4 Ends --%>
	<c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
		<c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
		<div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
	</c:if>
	
	<%--Start of changes for release 3.2.0 enhancement 5684 : 'Generate PCOF Task' button added for city user --%>
	<div style="WIDTH: 60%" class=floatLft>
		<p>Please complete the form below with information about the Procurement.</p>
		<p><span class="required">&#42;</span>Indicates required fields</p>
	</div>
	<%-- Code updated for R5 Starts--%>
	<portlet:resourceURL var='verifyPcofPSR' id='verifyPcofPSR' escapeXml='false'>
	</portlet:resourceURL>
	<input type="hidden" id="verifyPcofPSR" value="${verifyPcofPSR}"/>
	<c:choose>
		<c:when test="${procurementBean.status eq 2 and org_type eq 'city_org' and isOpenEndedOrZeroValue eq false and (Procurement.psrFlag ne '2'  || Procurement.generateTaskFlag eq '1')}">
			<div class=floatRht style="padding-top: 12px;">
				<INPUT class=button id="generatePCoFTask" title="Generate PCoF/PSR Tasks" value="Generate PCoF/PSR Tasks" type=button onclick="launchPCOF(${procurementId}, 'false');"/>
			</div>
		</c:when>
		<c:when test="${procurementBean.status eq 2 and org_type eq 'city_org' and isOpenEndedOrZeroValue eq true and (Procurement.psrFlag ne '2'  || Procurement.generateTaskFlag eq '1')}">
			<div class=floatRht style="padding-top: 12px;">
				<INPUT class=button id="generatePCoFTask" title="Generate PSR Task" value="Generate PSR Task" type=button onclick="launchPCOF(${procurementId}, 'true');"/>
			</div>
		</c:when>
	</c:choose>
	<c:if test="${Procurement.psrFlag eq '2'}">
		<div class=floatRht style="padding-top: 12px;">
			<input type="button" class="button" value="View PSR" onclick="viewPsr()"/>
		</div>
	</c:if>
	<%-- Code updated for R5 Ends--%>
	<%--End of changes for release 3.2.0 enhancement 5684 --%>
	<input type="hidden" id="lbProcurementStatusDraft" value="${lbProcurementStatusDraft}"/>
	<input type="hidden" id="lbProcurementStatusNotDraft" value="${lbProcurementStatusNotDraft}"/>
	<h3>Basic Information</h3>
	<div class="formcontainer">
		 <div class="row">
			 <span class="label">Procurement E-PIN&#58;</span>
			<span class="formfield">
			<c:choose>
					<c:when test="${Procurement.procurementEpin ne null}">
						<form:input path="procurementEpin" cssClass="input readonly" maxlength="20" cssStyle="width:150px" readonly="readonly" id="procurementEpin"/>
					</c:when>
					<c:otherwise>
						<form:input path="procurementEpin" cssClass="input readonly" value="Pending" maxlength="20" cssStyle="width:150px" readonly="readonly" id="procurementEpin"/>			
					</c:otherwise>
				</c:choose>
				<%-- check for organization type --%>
				<input type="button" id="assignepin" class='button agencyClass agencyClassButton' value="Assign E-PIN" />
			</span>
		</div>
		<div class="row">
			 <span class="label"><label class='required'>&#42;</label>Procurement Title&#58;</span>
			<span class="formfield">
				<form:input path="procurementTitle" cssClass="input agencyClass" maxlength="120" id="procurementTitle"/>
				</span>
				<span class="formfield error">
				<form:errors path="procurementTitle"  cssClass="ValidationError"></form:errors>
			</span>
		</div>
		
		<div class="row">
			 <span class="label">Procurement Status&#58;</span>
			<span class="formfield">
				<c:choose>
					<c:when test="${Procurement.procurementStatus ne null}">
						<form:hidden path="status"/>
						<form:input path="procurementStatus" cssClass="input readonly" id="procStatus"/>
					</c:when>
					<c:otherwise>
						<form:hidden path="status" value="1"/>
						<form:input path="procurementStatus" cssClass="input readonly" value="Draft" id="procStatus"/>			
					</c:otherwise>
				</c:choose>
			</span>
		</div>
		
		<input type="hidden" name="hiddenAgency" value="${Procurement.agencyId}">
		<input type="hidden" name="hiddenOpenEndedFlag" value="${Procurement.isOpenEndedRFP}">
		<div class="row">
			 <span class="label"><label class='required'>&#42;</label>Agency&#58;</span>
					<span class="formfield">
						<form:select path="agencyId" cssClass="input agencyClassDropDown" id="agencyId">
						
						<form:option id="-1" value=""> </form:option>
							<form:options items="${nycAgency}" />
						</form:select>
						</span>			
				<span class="formfield error">
				<form:errors path="agencyId" cssClass="ValidationError"></form:errors>
			</span>
		</div>
		
		<div class="row" id="progDivId">
			<div>
				 <span class="label"><label class='required'>&#42;</label>Program Name&#58;</span>
				 <span class="formfield">
					<form:select path="programName" cssClass="input agencyClassDropDown" id="programNameId">
						<form:option id="-1" value=""></form:option>
						<c:forEach var="programNameVar" items="${programNameList}">
						<c:choose>
						<c:when test="${Procurement ne null}">
							<c:choose>
							<c:when test="${Procurement.programId eq programNameVar.programId or Procurement.programName eq programNameVar.programId}">
								<option selected="selected" value="${programNameVar.programId}">${programNameVar.programName}</option>
							</c:when>
							<c:otherwise>
				                   <option value="${programNameVar.programId}">${programNameVar.programName}</option>
				           	</c:otherwise>
						</c:choose>
						</c:when>
						<c:otherwise>
							<option value="${programNameVar.programId}">${programNameVar.programName}</option>			
						</c:otherwise>
					</c:choose>
						</c:forEach>
					</form:select>
					</span>
					<span class="formfield error">
					<form:errors path="programName"  cssClass="ValidationError"></form:errors>
				</span>
			</div>
		</div>
		<div class="row">
			 <span class="label"><label class='required'>&#42;</label>Accelerator Primary Contact&#58;</span>
			<span class="formfield">
				<form:select path="accPrimaryContact" cssClass="input agencyClassDropDown" id="accPrimaryContact">
					<form:option id="-1" value=""></form:option>
					<c:forEach var="acceleratorUser" items="${accUserList}">
					<c:choose>
					<c:when test="${Procurement ne null}">
						<c:choose>
							<c:when test="${Procurement.accPrimaryContact eq acceleratorUser.msStaffId}">
								<option selected="selected" value="${acceleratorUser.msStaffId}">${acceleratorUser.msStaffFirstName}</option>
							</c:when>
							<c:otherwise>
				                 <option value="${acceleratorUser.msStaffId}">${acceleratorUser.msStaffFirstName}</option>
				           	</c:otherwise>
				           	</c:choose>
				     </c:when>
				     <c:otherwise>
				           	<option value="${acceleratorUser.msStaffId}">${acceleratorUser.msStaffFirstName}</option>
				     </c:otherwise>
						
		            </c:choose>        
					</c:forEach>
				</form:select>
				</span>
				<span class="formfield error">
				<form:errors path="accPrimaryContact"  cssClass="ValidationError"></form:errors>
			</span>
		</div>
		<div class="row">
			 <span class="label"><label class='required'>&#42;</label>Accelerator Secondary Contact&#58;</span>
			<span class="formfield">
				<form:select path="accSecondaryContact" cssClass="input agencyClassDropDown" id="accSecondaryContact">
					<form:option id="-1" value=""></form:option>
					<c:forEach var="acceleratorUser" items="${accUserList}">
		                    <c:choose>
						<c:when test="${Procurement.accSecondaryContact eq acceleratorUser.msStaffId}">
							<option selected="selected" value="${acceleratorUser.msStaffId}">${acceleratorUser.msStaffFirstName}</option>
						</c:when>
						<c:otherwise>
			                   <option value="${acceleratorUser.msStaffId}">${acceleratorUser.msStaffFirstName}</option>
			           	</c:otherwise>
					</c:choose>
					</c:forEach>
				</form:select>
				</span>
				<span class="formfield error">
				<form:errors path="accSecondaryContact"  cssClass="ValidationError"></form:errors>
			</span>
		</div>
		<div class="row" id="agencyPrimaryId">
		<div>
			 <span class="label"><label class='required'>&#42;</label>Agency Primary Contact&#58;</span>
			<span class="formfield">
				<form:select path="agecncyPrimaryContact" cssClass="input agencyClassDropDown" id="agecncyPrimaryContact" onchange="callPrimaryContact(this)">
					<form:option id="-1" value=""></form:option>
					<c:forEach var="acceleratorUser" items="${agencyUserList}">
		                    <c:choose>
						<c:when test="${Procurement.agecncyPrimaryContact eq acceleratorUser.msStaffId}">
							<option selected="selected" value="${acceleratorUser.msStaffId}">${acceleratorUser.msStaffFirstName}</option>
						</c:when>
						<c:otherwise>
			                   <option value="${acceleratorUser.msStaffId}">${acceleratorUser.msStaffFirstName}</option>
			           	</c:otherwise>
					</c:choose>
					</c:forEach>
				</form:select>
				</span>
				<span class="formfield error">
				<form:errors path="agecncyPrimaryContact"  cssClass="ValidationError"></form:errors>
			</span>
			</div>
		</div>
		<div class="row" id="agencySecId">
		<div>
			 <span class="label"><label class='required'>&#42;</label>Agency Secondary Contact&#58;</span>
			<span class="formfield">
				<form:select path="agecncySecondaryContact" cssClass="input agencyClassDropDown" id="agecncySecondaryContact">
					<form:option id="-1" value=""></form:option>
					<c:forEach var="acceleratorUser" items="${agencyUserList}">
		                    <c:choose>
						<c:when test="${Procurement.agecncySecondaryContact eq acceleratorUser.msStaffId}">
							<option selected="selected" value="${acceleratorUser.msStaffId}">${acceleratorUser.msStaffFirstName}</option>
						</c:when>
						<c:otherwise>
			                   <option value="${acceleratorUser.msStaffId}">${acceleratorUser.msStaffFirstName}</option>
			           	</c:otherwise>
					</c:choose>
					</c:forEach>
				</form:select>
				</span>
				<span class="formfield error">
				<form:errors path="agecncySecondaryContact"  cssClass="ValidationError"></form:errors>
			</span>
			</div>
		</div>
		<div class="row">
			 <span class="label"><label class='required'>&#42;</label>Agency Email Contact&#58;</span>
			<span class="formfield">
				<form:input path="email" cssClass="input agencyClass" id="email" maxlength="60"/>
				</span>
				<span class="formfield error">
				<form:errors path="email"  cssClass="ValidationError"></form:errors>
			</span>
		</div>
		<!-- Changes for 8322 starts -->
		<c:set var="agencyDisabled"></c:set>
			<c:if test="${org_type eq 'agency_org' || Procurement.procurementStatus eq 'Closed' || Procurement.procurementStatus eq 'Cancelled'}">
				<c:set var="agencyDisabled">true</c:set>	
			</c:if>
		<!-- Changes for 8322 ends -->
		<!-- start QC 9091 disable Procurement description for read only users for RFP -->
		<%
  		if( CommonUtil.hideForOversightRole(request.getSession()) ){ %>
			<c:set var="agencyDisabled" value="${true}"></c:set>	
		<%}
		%> 	
		<!-- end QC 9091 disable  -->
		<textarea id="procDescription" style="display:none">${Procurement.procurementDescription}</textarea>
		<div class="row">
			 <span class="label" style='height:400px'><label class='required'>&#42;</label>Procurement Description&#58;</span>
			<span class="formfield">
				<form:textarea path="procurementDescription" disabled="${agencyDisabled}" cssClass="input agencyClass descriptionField" id="procurementDescription" maxlength="3500"></form:textarea>
				</span>
				<span class="error clear" style='margin-left:36%'>
					<form:errors path="procurementDescription"  cssClass="ValidationError"></form:errors>
				</span>
		</div>
		<div class="row">
		<%-- Code updated for R4 Starts --%>
			 <span class="label"><label class='required'>&#42;</label>Is this an open-ended RFP?:</span>
			 <span class="formfield">
			 	<form:select cssClass="input agencyClassDropDown" path="isOpenEndedRFP" id='isOpenEndedRFP' value="1" title="If this is an open ended procurement with no proposal due date, select the Yes option.">
			 		<option value="0" title="No">No</option>
					<c:choose>
						<c:when test="${Procurement.isOpenEndedRFP eq '1'}">
							<option value="1" title="Yes" selected="selected">Yes</option>
						</c:when>
						<c:otherwise>
							<option value="1" title="Yes">Yes</option>
			           	</c:otherwise>
					</c:choose>
			 	</form:select>
			 </span>
			 <span class="formfield error">
				<form:errors path="isOpenEndedRFP"  cssClass="ValidationError"></form:errors>
			 </span>
		</div>
		<div class="row">
			 <%-- Code updated for R4 Ends --%>
			 <span class="label"><label class='required'>&#42;</label>Estimated No. of Contracts&#58;</span>
			<span class="formfield">
				<form:input path="estNumberOfContracts" validate="number" cssClass="input agencyClass" id="estNumberOfContracts" maxlength="4"/>
				</span>
				<span class="formfield error" id="estNumberOfContractsId">
				<form:errors path="estNumberOfContracts"  cssClass="ValidationError"></form:errors>
			</span>
		</div>
		<div class="row">
			 <span class="label"><label class='required'>&#42;</label>Estimated Procurement Value ($)&#58;</span>
			<span class="formfield">
				<form:input path="estProcurementValue"  ${readOnlySection} cssClass="input agencyClass" id="estProcurementValue" />
				</span>
				<span class="formfield error">
				<form:errors path="estProcurementValue"  cssClass="ValidationError"></form:errors>
			</span>
		</div>
		<div class="row">
			 <span class="label">Link to Concept Report:</span>
			<span class="formfield">
				<form:input path="linkToConceptReport" cssClass="input agencyClass" id="linkToConceptReport" maxlength="250" title="Link must be formatted in http:// or https:// form."/>
				</span>
				<span class="formfield error">
				<form:errors path="linkToConceptReport"  cssClass="ValidationError"></form:errors>
			</span>
		</div>
		<div class="row">
			 <span class="label">Display Total Number of Service Units and Cost per Service Unit?</span>
			 <span class="formfield"><form:checkbox path="serviceUnitRequiredFlag" id='serviceUnitRequired' value="1" cssClass="agencyClassDropDown"/></span>
			 <span class="formfield error">
				<form:errors path="serviceUnitRequiredFlag"  cssClass="ValidationError"></form:errors>
			 </span>
		</div>
		<input type="hidden" value="${Procurement.serviceUnitRequiredFlag}" id="serviceUnitHiddenFlag"/>
	</div>
	
	<h3 >Procurement Dates</h3>
	<div class="formcontainer addProcurement" id="addProcurementId">
		<div class="row">
			 <span class="label"  style='background:none'></span>
			<span class="formfield">
				<span>Planned Dates</span>
				</span>
				<span class="formfield">
				<span>Updated Dates</span>
			</span>
		</div>
		<c:if test="${Procurement.procurementId ne null and Procurement.procurementId ne ''}">
		<fmt:parseDate value="${Procurement.rfpReleaseDatePlanned}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="rfpReleaseDatePlannedFormatted"/>
		<fmt:parseDate value="${Procurement.rfpReleaseDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="rfpReleaseDateUpdatedFormatted"/>
		</c:if>
		<c:if test="${Procurement.status ne null and Procurement.status ne '1'}">
	 		<form:hidden path="rfpReleaseDatePlanned" value="${rfpReleaseDatePlannedFormatted}"/>
	 	</c:if>
		 <div class="row">
			 <span class="label"><label class='required'>&#42;</label>RFP Release Date&#58;</span>
			  <span class="formfield">
				<span>
					<form:input path="rfpReleaseDatePlanned"  value="${rfpReleaseDatePlannedFormatted}" 
					maxlength="10" validate="calenderFormat" futureDate="true"
							cssStyle="width:78px;" id="rfpReleaseDatePlanned" cssClass="StatusOtherThanDraft agencyClass"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassPlanned"  onclick="NewCssCal('rfpReleaseDatePlanned',event,'mmddyyyy');return false;">
									<br/>
									<form:errors path="rfpReleaseDatePlanned"  cssClass="ValidationError"></form:errors>
				</span><span class="error"></span>
			</span>
			<span class="formfield">
				<span>			   	
					<form:input path="rfpReleaseDateUpdated" value="${rfpReleaseDateUpdatedFormatted}" maxlength="10" validate="calenderFormat" futureDate="true" readonly="readonly" cssClass='draftStatus agencyClass' 
								cssStyle="width:78px;" id="rfpReleaseDateUpdated"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassUpdated"  onclick="NewCssCal('rfpReleaseDateUpdated',event,'mmddyyyy');return false;">
							   	 <br/>	
							<form:errors path="rfpReleaseDateUpdated"  cssClass="ValidationError"></form:errors>
									   	
				</span>		<span class="error" ></span>		   
			</span>
		</div>
		 	 <c:if test="${Procurement.status ne null and (Procurement.status eq '3' or Procurement.status eq '4' or Procurement.status eq '5' or Procurement.status eq '6')}">
		 	 <form:hidden path="rfpReleaseDateUpdated" value="${rfpReleaseDateUpdatedFormatted}"/>
				<c:set var="rfpReleaseDateFormatted"><fmt:formatDate pattern='MM/dd/yyyy' value='${Procurement.rfpReleaseDate}'/></c:set>
				<c:set var="rfpReleaseTimeFormatted"><fmt:formatDate type="time" value='${Procurement.rfpReleaseDate}'/></c:set>
				<c:set var="sectionRFPReleaseDate"><%=HHSComponentMappingConstant.S203_RFP_RELEASE_DATE%></c:set>
					<d:content section="${sectionRFPReleaseDate}">				
						<div class='infoMessage' id="messagediv" style="display:block">The RFP was released on <fmt:formatDate pattern="MM/dd/yyyy @ hh:mm a" type="both"  value="${Procurement.rfpReleaseDate}" /></div>
					</d:content>
			</c:if>
		
		<c:if test="${Procurement.procurementId ne null and Procurement.procurementId ne ''}">
		<fmt:parseDate value="${Procurement.preProposalConferenceDatePlanned}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="preProposalConferenceDatePlannedFormatted"/>
		<fmt:parseDate value="${Procurement.preProposalConferenceDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="preProposalConferenceDateUpdatedFormatted"/>
		</c:if>
		<c:if test="${Procurement.status ne null and Procurement.status ne '1'}">
	 		<form:hidden path="preProposalConferenceDatePlanned" value="${preProposalConferenceDatePlannedFormatted}"/>
	 	</c:if>
		 <div class="row openEndedHide">
			 <span class="label">Pre-Proposal Conference Date&#58;</span>
			<span class="formfield">
				<span>
					<form:input path="preProposalConferenceDatePlanned" value="${preProposalConferenceDatePlannedFormatted}" maxlength="10" validate="calenderFormat"   cssStyle="width:78px;" id="preProposalConferenceDatePlanned" cssClass="StatusOtherThanDraft agencyClass"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassPlanned"  onclick="NewCssCal('preProposalConferenceDatePlanned',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="preProposalConferenceDatePlanned"  cssClass="ValidationError"></form:errors>
						</span>	   	<span class="error" ></span>
				
				</span>
				<span class="formfield">
				<span>
					<form:input path="preProposalConferenceDateUpdated" value="${preProposalConferenceDateUpdatedFormatted}" maxlength="10" validate="calenderFormat" readonly="readonly" cssClass='draftStatus agencyClass'  cssStyle="width:78px;" id="conferenceDateToId"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassUpdated"  onclick="NewCssCal('conferenceDateToId',event,'mmddyyyy');return false;">	<br/>
							   	<form:errors path="preProposalConferenceDateUpdated"  cssClass="ValidationError"></form:errors>
							  
				</span> <span class="error"></span>
			</span>
		</div>
		<c:if test="${Procurement.procurementId ne null and Procurement.procurementId ne ''}">
		<fmt:parseDate value="${Procurement.proposalDueDatePlanned}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="proposalDueDatePlannedFormatted"/>
		<fmt:parseDate value="${Procurement.proposalDueDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="proposalDueDateUpdatedFormatted"/>
		</c:if>
		<c:if test="${Procurement.status ne null and Procurement.status ne '1'}">
	 		<form:hidden path="proposalDueDatePlanned" value="${proposalDueDatePlannedFormatted}"/>
	 	</c:if>
		 <div class="row openEndedHide">
			 <span class="label"><label class='required'>&#42;</label>Proposal Due Date&#58;</span>
			<span class="formfield">
				<span>
					<form:input path="proposalDueDatePlanned" value="${proposalDueDatePlannedFormatted}" maxlength="10" validate="calenderFormat"  futureDate="true" cssStyle="width:78px;" id="proposalDueDateFromId" cssClass="StatusOtherThanDraft agencyClass"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassPlanned"  onclick="NewCssCal('proposalDueDateFromId',event,'mmddyyyy');return false;">
							   	<fmt:message key="AT_THE_RATE_SYMBOL"/> ${releaseTime}<br/>
							   	<form:errors path="proposalDueDatePlanned"  cssClass="ValidationError"></form:errors>
				</span>	<span class="error"></span>
				</span>
				<span class="formfield">
				<span>
					<form:input path="proposalDueDateUpdated" value="${proposalDueDateUpdatedFormatted}" maxlength="10" validate="calenderFormat" futureDate="true" readonly="readonly" cssClass='draftStatus agencyClass'  cssStyle="width:78px;" id="proposalDueDateToId"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassUpdated"  onclick="NewCssCal('proposalDueDateToId',event,'mmddyyyy');return false;">
							   	<fmt:message key="AT_THE_RATE_SYMBOL"/>  ${releaseTime}<br/>
							   	<form:errors path="proposalDueDateUpdated"  cssClass="ValidationError"></form:errors>
				</span>	<span class="error"></span>
								
			</span>
		</div>
		<input type="hidden" id="submissionCloseDateId" value="${Procurement.submissionCloseDate}">
		
		<c:if test="${Procurement.submissionCloseDate ne null}">
			<form:hidden path="proposalDueDateUpdated" value="${proposalDueDateUpdatedFormatted}"/>
			<c:set var="proposalDueDate"><fmt:formatDate pattern='MM/dd/yyyy' value='${Procurement.submissionCloseDate}'/></c:set>
			<c:set var="proposalDueTime"><fmt:formatDate type="time" value='${Procurement.submissionCloseDate}'/></c:set>
				<c:set var="sectionRFPReleaseDate"><%=HHSComponentMappingConstant.S203_RFP_RELEASE_DATE%></c:set>
				<d:content section="${sectionRFPReleaseDate}">
					<div class="infoMessage" id="messagediv" style="display:block">Submissions were closed on <fmt:formatDate pattern="MM/dd/yyyy @ hh:mm a" type="both"  value="${Procurement.submissionCloseDate}" /></div>
				</d:content>
		</c:if>
		
	</div>
		<c:if test="${Procurement.procurementId ne null and Procurement.procurementId ne ''}">
		<fmt:parseDate value="${Procurement.firstRFPEvalDatePlanned}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="firstRFPEvalDatePlannedFormatted"/>
		<fmt:parseDate value="${Procurement.firstRFPEvalDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="firstRFPEvalDateUpdatedFormatted"/>
		</c:if>
		<c:if test="${Procurement.status ne null and Procurement.status ne '1'}">
	 		<form:hidden path="firstRFPEvalDatePlanned" value="${firstRFPEvalDatePlannedFormatted}"/>
	 	</c:if>
	<h3 class="openEndedHide">Evaluation Dates</h3>
	<div class="formcontainer addProcurement openEndedHide" id="addProcurementId1">
		 <div class="row">
			 <span class="label"><label class='required'>&#42;</label>First Draft of RFP &amp; Evaluation Criteria Date&#58;</span>
			<span class="formfield">
				<span>
					<form:input path="firstRFPEvalDatePlanned"  value="${firstRFPEvalDatePlannedFormatted}" maxlength="10" validate="calenderFormat"  cssStyle="width:78px;" id="firstRFPEvalDateFrom" cssClass="StatusOtherThanDraft agencyClass" />
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassPlanned"  onclick="NewCssCal('firstRFPEvalDateFrom',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="firstRFPEvalDatePlanned"  cssClass="ValidationError"></form:errors>
							   
				</span>	<span class="error"></span>
				</span>
				<span class="formfield">
				<span>
					<form:input path="firstRFPEvalDateUpdated" value="${firstRFPEvalDateUpdatedFormatted}" maxlength="10" validate="calenderFormat" readonly="readonly" cssClass='draftStatus agencyClass'  cssStyle="width:78px;" id="firstRFPEvalDateTo"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassUpdated"  onclick="NewCssCal('firstRFPEvalDateTo',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="firstRFPEvalDateUpdated"  cssClass="ValidationError"></form:errors>
							   
				</span>	<span class="error"></span>
			</span>
		</div>
		<c:if test="${Procurement.procurementId ne null and Procurement.procurementId ne ''}">
		<fmt:parseDate value="${Procurement.finalRFPEvalDatePlanned}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="finalRFPEvalDatePlannedFormatted"/>
		<fmt:parseDate value="${Procurement.finalRFPEvalDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="finalRFPEvalDateUpdatedFormatted"/>
		</c:if>
		<c:if test="${Procurement.status ne null and Procurement.status ne '1'}">
	 		<form:hidden path="finalRFPEvalDatePlanned" value="${finalRFPEvalDatePlannedFormatted}"/>
	 	</c:if>
		 <div class="row">
			 <span class="label"><label class='required'>&#42;</label>Finalize RFP &amp; Evaluation Criteria Date&#58;</span>
			<span class="formfield">
				<span>
					<form:input path="finalRFPEvalDatePlanned"  value="${finalRFPEvalDatePlannedFormatted}" maxlength="10" validate="calenderFormat"  cssStyle="width:78px;" id="finalRFPEvalDatePlanned" cssClass="StatusOtherThanDraft agencyClass"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassPlanned"  onclick="NewCssCal('finalRFPEvalDatePlanned',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="finalRFPEvalDatePlanned"  cssClass="ValidationError"></form:errors>
							  
				</span> 	<span class="error"></span>
				</span>
				<span class="formfield">
				<span>
					<form:input path="finalRFPEvalDateUpdated" value="${finalRFPEvalDateUpdatedFormatted}" maxlength="10" validate="calenderFormat" readonly="readonly" cssClass='draftStatus agencyClass'  cssStyle="width:78px;" id="finalRFPEvalDateUpdated"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassUpdated"  onclick="NewCssCal('finalRFPEvalDateUpdated',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="finalRFPEvalDateUpdated"  cssClass="ValidationError"></form:errors>
							   	
				</span><span class="error"></span>
			</span>
		</div>
		<c:if test="${Procurement.procurementId ne null and Procurement.procurementId ne ''}">
		<fmt:parseDate value="${Procurement.evaluatorTrainingDatePlanned}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="evaluatorTrainingDatePlannedFormatted"/>
		<fmt:parseDate value="${Procurement.evaluatorTrainingDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="evaluatorTrainingDateUpdatedFormatted"/>
		</c:if>
		<c:if test="${Procurement.status ne null and Procurement.status ne '1'}">
	 		<form:hidden path="evaluatorTrainingDatePlanned" value="${evaluatorTrainingDatePlannedFormatted}"/>
	 	</c:if>
		 <div class="row">
			 <span class="label"><label class='required'>&#42;</label>Evaluator Training Date&#58;</span>
			<span class="formfield">
				<span>
					<form:input path="evaluatorTrainingDatePlanned"  value="${evaluatorTrainingDatePlannedFormatted}"  maxlength="10" validate="calenderFormat"  cssStyle="width:78px;" id="evaluatorTrainingDatePlanned" cssClass="StatusOtherThanDraft agencyClass"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassPlanned"  onclick="NewCssCal('evaluatorTrainingDatePlanned',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="evaluatorTrainingDatePlanned"  cssClass="ValidationError"></form:errors>
							   
				</span>	<span class="error"></span>
				</span>
				<span class="formfield">
				<span>
					<form:input path="evaluatorTrainingDateUpdated" value="${evaluatorTrainingDateUpdatedFormatted}" maxlength="10" validate="calenderFormat" readonly="readonly" cssClass='draftStatus agencyClass' cssStyle="width:78px;" id="evaluatorTrainingDateUpdated"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassUpdated"  onclick="NewCssCal('evaluatorTrainingDateUpdated',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="evaluatorTrainingDateUpdated"  cssClass="ValidationError"></form:errors>
							  
				</span> 	<span class="error"></span>
			</span>
		</div>
		<c:if test="${Procurement.procurementId ne null and Procurement.procurementId ne ''}">
		<fmt:parseDate value="${Procurement.firstEvalCompletionDatePlanned}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="firstEvalCompletionDatePlannedFormatted"/>
		<fmt:parseDate value="${Procurement.firstEvalCompletionDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="firstEvalCompletionDateUpdatedFormatted"/>
		</c:if>
		<c:if test="${Procurement.status ne null and Procurement.status ne '1'}">
	 		<form:hidden path="firstEvalCompletionDatePlanned" value="${firstEvalCompletionDatePlannedFormatted}"/>
	 	</c:if>
		<div class="row">
			 <span class="label"><label class='required'>&#42;</label>First Round of Evaluation Completion Date&#58;</span>
			<span class="formfield">
				<span>
					<form:input path="firstEvalCompletionDatePlanned" value="${firstEvalCompletionDatePlannedFormatted}" maxlength="10" validate="calenderFormat"  cssStyle="width:78px;" id="firstEvalCompletionDatePlanned" cssClass="StatusOtherThanDraft agencyClass" />
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassPlanned"  onclick="NewCssCal('firstEvalCompletionDatePlanned',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="firstEvalCompletionDatePlanned"  cssClass="ValidationError"></form:errors>
							   
				</span>	<span class="error"></span>
				</span>
				<span class="formfield">
				<span>
					<form:input path="firstEvalCompletionDateUpdated" value="${firstEvalCompletionDateUpdatedFormatted}" maxlength="10" validate="calenderFormat" readonly="readonly" cssClass='draftStatus agencyClass'  cssStyle="width:78px;" id="firstEvalCompletionDateUpdated"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassUpdated"  onclick="NewCssCal('firstEvalCompletionDateUpdated',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="firstEvalCompletionDateUpdated"  cssClass="ValidationError"></form:errors>
							 
				</span>  	<span class="error"></span>
			</span>
		</div>
		<c:if test="${Procurement.procurementId ne null and Procurement.procurementId ne ''}">
		<fmt:parseDate value="${Procurement.finalEvalCompletionDatePlanned}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="finalEvalCompletionDatePlannedFormatted"/>
		<fmt:parseDate value="${Procurement.finalEvalCompletionDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="finalEvalCompletionDateUpdatedFormatted"/>
		</c:if>
		<c:if test="${Procurement.status ne null and Procurement.status ne '1'}">
	 		<form:hidden path="finalEvalCompletionDatePlanned" value="${finalEvalCompletionDatePlannedFormatted}"/>
	 	</c:if>
		<div class="row">
			 <span class="label"><label class='required'>&#42;</label>Finalize Evaluation Date&#58;</span>
			<span class="formfield">
				<span>
					<form:input path="finalEvalCompletionDatePlanned" value="${finalEvalCompletionDatePlannedFormatted}" maxlength="10" validate="calenderFormat" cssStyle="width:78px;" id="finalEvalCompletionDatePlanned" cssClass="StatusOtherThanDraft agencyClass" />
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassPlanned"  onclick="NewCssCal('finalEvalCompletionDatePlanned',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="finalEvalCompletionDatePlanned"  cssClass="ValidationError"></form:errors>
							   	
				</span><span class="error"></span>
				</span>
				<span class="formfield">
				<span>
					<form:input path="finalEvalCompletionDateUpdated"  value="${finalEvalCompletionDateUpdatedFormatted}" maxlength="10" validate="calenderFormat" readonly="readonly" cssClass='draftStatus agencyClass' cssStyle="width:78px;" id="finalEvalCompletionDateUpdated" />
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassUpdated"  onclick="NewCssCal('finalEvalCompletionDateUpdated',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="finalEvalCompletionDateUpdated"  cssClass="ValidationError"></form:errors>
							   
				</span>	<span class="error"></span>
			</span>
		</div>
		
		<c:if test="${Procurement.procurementId ne null and Procurement.procurementId ne ''}">
		<fmt:parseDate value="${Procurement.awardSelectionDatePlanned}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="awardSelectionDatePlannedFormatted"/>
		<fmt:parseDate value="${Procurement.awardSelectionDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="awardSelectionDateUpdatedFormatted"/>
		</c:if>
		<c:if test="${Procurement.status ne null and Procurement.status ne '1'}">
	 		<form:hidden path="awardSelectionDatePlanned" value="${awardSelectionDatePlannedFormatted}"/>
	 	</c:if>
		<div class="row">
			 <span class="label"><label class='required'>&#42;</label>Award Selection Date&#58;</span>
			<span class="formfield">
				<span>
					<form:input path="awardSelectionDatePlanned" value="${awardSelectionDatePlannedFormatted}" maxlength="10" validate="calenderFormat"  cssStyle="width:78px;" id="awardSelectionDatePlanned" cssClass="StatusOtherThanDraft agencyClass" />
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassPlanned"  onclick="NewCssCal('awardSelectionDatePlanned',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="awardSelectionDatePlanned"  cssClass="ValidationError"></form:errors>
							   
				</span>	<span class="error"></span>
				</span>
				<span class="formfield">
				<span>
					<form:input path="awardSelectionDateUpdated" value="${awardSelectionDateUpdatedFormatted}" maxlength="10" validate="calenderFormat" readonly="readonly" cssClass='draftStatus agencyClass'  cssStyle="width:78px;" id="awardSelectionDateUpdated"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassUpdated"  onclick="NewCssCal('awardSelectionDateUpdated',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="awardSelectionDateUpdated"  cssClass="ValidationError"></form:errors>
							   	
				</span><span class="error"></span>
			</span>
		</div>
	</div>
		<c:if test="${Procurement.procurementId ne null and Procurement.procurementId ne ''}">
		<fmt:parseDate value="${Procurement.contractStartDatePlanned}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="contractStartDatePlannedFormatted"/>
		<fmt:parseDate value="${Procurement.contractStartDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="contractStartDateUpdatedFormatted"/>
		</c:if>
		<c:if test="${Procurement.status ne null and Procurement.status ne '1'}">
	 		<form:hidden path="contractStartDatePlanned" value="${contractStartDatePlannedFormatted}"/>
	 	</c:if>
	<h3 class="openEndedHide">Contract Dates</h3>
	<div class="formcontainer addProcurement openEndedHide" id="addProcurementId2">
		<div class="row">
			 <span class="label"><label class='required'>&#42;</label>Contract Start Date&#58;</span>
			<span class="formfield">
				<span>
					<form:input path="contractStartDatePlanned" value="${contractStartDatePlannedFormatted}" maxlength="10" validate="calenderFormat"  futureDate="true" cssStyle="width:78px;" id="contractStartDatePlanned" cssClass="StatusOtherThanDraft agencyClass" />
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassPlanned"  onclick="NewCssCal('contractStartDatePlanned',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="contractStartDatePlanned"  cssClass="ValidationError"></form:errors>
							   
				</span>	<span class="error"></span>
				</span>
				<span class="formfield">
				<span>
					<form:input path="contractStartDateUpdated" value="${contractStartDateUpdatedFormatted}" maxlength="10" validate="calenderFormat" futureDate="true" readonly="readonly" cssClass='draftStatus agencyClass'  cssStyle="width:78px;" id="contractStartDateUpdated"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassUpdated"  onclick="NewCssCal('contractStartDateUpdated',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="contractStartDateUpdated"  cssClass="ValidationError"></form:errors>
							   	
				</span><span class="error"></span>
			</span>
		</div>
		<c:if test="${Procurement.procurementId ne null and Procurement.procurementId ne ''}">
		<fmt:parseDate value="${Procurement.contractEndDatePlanned}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="contractEndDatePlannedFormatted"/>
		<fmt:parseDate value="${Procurement.contractEndDateUpdated}" pattern="yyyy-MM-dd" var="formatedDate"/> 
		<fmt:formatDate value="${formatedDate}" pattern="MM/dd/yyyy" var="contractEndDateUpdatedFormatted"/>
		</c:if>
		<c:if test="${Procurement.status ne null and Procurement.status ne '1'}">
	 		<form:hidden path="contractEndDatePlanned" value="${contractEndDatePlannedFormatted}"/>
	 	</c:if>
		<div class="row">
			 <span class="label"><label class='required'>&#42;</label>Contract End Date&#58;</span>
			<span class="formfield">
				<span>
					<form:input path="contractEndDatePlanned" value="${contractEndDatePlannedFormatted}" maxlength="10" validate="calenderFormat"  futureDate="true"  cssStyle="width:78px;" id="contractEndDatePlanned" cssClass="StatusOtherThanDraft agencyClass" />
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassPlanned"  onclick="NewCssCal('contractEndDatePlanned',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="contractEndDatePlanned"  cssClass="ValidationError"></form:errors>
							   	
				</span><span class="error"></span>
				</span>
				<span class="formfield">
				<span>
					<form:input path="contractEndDateUpdated" value="${contractEndDateUpdatedFormatted}" maxlength="10" validate="calenderFormat"  futureDate="true" readonly="readonly" cssClass='draftStatus agencyClass'  cssStyle="width:78px;" id="contractEndDateUpdated"/>
							   	<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/calender.png" class="imgclassUpdated"  onclick="NewCssCal('contractEndDateUpdated',event,'mmddyyyy');return false;"><br/>
							   	<form:errors path="contractEndDateUpdated"  cssClass="ValidationError"></form:errors>
							   	
				</span><span class="error"></span>
			</span>
		</div>
	</div>
		<!-- R6-ref epin id sent to server start -->
		<form:input type='hidden' name="hiddenRefEpin" id='hiddenRefEpin' path="refEpinId" value="" />
		<!-- R6-ref epin id sent to server start -->
		<form:hidden path="procurementId" value="${procurementId}"/>
		<%-- Start : Changes in R5 --%>
		<form:hidden path="psrDetailsId" value="${psrDetailsId}"/>
		<%-- End : Changes in R5 --%>
		<c:if test="${Procurement.lastPublishDate ne null}">
				<c:set var="lastPublishDateTemp"><fmt:formatDate pattern='MM/dd/yyyy' value='${Procurement.lastPublishDate}'/></c:set>
				<form:hidden path="lastPublishedByUser"/>
				<form:hidden path="lastPublishDate" value="${lastPublishDateTemp}"/>
		</c:if>
	<div class="buttonholder">
	<div class='floatLft'>
		<comSM:commonSolicitation screenName="ProcurementSummary" level="ProcurementWidget" procurementId="${procurementId}" procurementStatus="${procurementStatus}"></comSM:commonSolicitation>
		</div>
		
		<c:if test="${readOnlySection eq null}">
			<c:set var="sectionRFPReleaseDate"><%=HHSComponentMappingConstant.S203_SAVE_BUTTON%></c:set>
			<d:content section="${sectionRFPReleaseDate}">		
				<input type="submit" value="Save" title='Save changes' id="save"/>
			</d:content>
		</c:if>
			
	</div>
	</d:content>
	</div>
	<div class="alert-box alert-box-assignEpin">
		<div class="content">
			<div class="tabularCustomHead">Assign APT Procurement E-PIN</div> 
			<div class='tabularContainer'>
				<h2>Assign APT Procurement E-PIN</h2>
				<div class='hr'></div>
				<div id="searchEpin">
					<div class="formcontainer">
						<div class="row">
							<span class="label equalForms clearLabel">
							<%-- R6 : Removed maxlength= 10 for new Epin formats --%>
								<input name="epin" type="text" style='width: 100%;' id="epin">
							</span> 
							<span class="formfield equalForms">
								<input type="button" class="button" value="Find E&ndash;PIN" id="searchepinbutton" onclick="populateEpinDetails()" disabled="disabled"/> 
							</span>
							
						</div>
							<span class="error searchepingclassError" id="epinError"></span>
						<div class="row">
							<span class="label equalForms">Procurement E-PIN</span> 
							<span class="formfield equalForms searchepingclass1">-- </span>
						</div>
						<div class="row">
							<span class="label equalForms">Procurement Start Date</span>
							<span class="formfield equalForms searchepingclass2">-- </span>
						</div>
						<div class="row">
							<span class="label equalForms">Agency</span> 
							<span class="formfield equalForms searchepingclass3">-- </span>
						</div>
						<div class="row">
							<span class="label equalForms">Agency ID</span> 
							<span class="formfield equalForms searchepingclass4">-- </span>
						</div>
						<div class="row">
							<span class="label equalForms">Project/Program</span> 
							<span class="formfield equalForms searchepingclass5">-- </span>
						</div>
						<div class="row">
							<span class="label equalForms">Description</span> 
							<span class="formfield equalForms searchepingclass6">-- </span>
						</div>
					</div>
					<div class="buttonholder">
						<input type="button" class="graybtutton" value="Cancel" id="cancelSearchEpin"/> 
						<input type="button" class="button" id="assignepinfinal" value="Assign E&ndash;PIN" disabled="disabled" onclick="setEpinId()"/>
					</div>
				</div>
			</div>			
		</div>
		<a  href="javascript:void(0)" class="exit-panel assignepin-exit">&nbsp;</a>
	</div>
	<div class="alert-box alert-box-closeProcurement">
		<div class='tabularCustomHead'>Close Procurement</div>
		<a href="javascript:void(0);" class="exit-panel close-procurement">&nbsp;</a>
		<div id="requestAdvance"></div>
	</div>
	<div class="alert-box alert-box-cancelProcurement">
		<div class='tabularCustomHead'>Cancel Procurement</div>
		<a href="javascript:void(0);" class="exit-panel cancel-procurement">&nbsp;</a>
		<div id="requestCancel"></div>
	</div>
	<div class="overlay"></div>
</form:form>
</nav:navigationSM>