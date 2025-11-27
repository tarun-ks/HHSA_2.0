<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>

<%--This JSP is for Add Contract screen S302 in Financials tab to add new contracts.  --%>

<style>
.contractPopup span.error{
	width:44% !important
}
.contractPopup .date{
	width:88% !important
}
.tabularContainer{
		overflow-y:auto;
		height:640px
	}
	.button{
		position:static !important
	}
.tabularContainer .formcontainer .row span.error{
	margin-left:49%
}
.alert-box-confirmFiscalYear {
    background: #FFF;
    display: none;
    z-index: 1001;
      position:fixed !important;
      top:25% !important
}

</style>
<portlet:defineObjects />
<%--  Overlay Popup Starts --%>
<%-- This Portlet resource URL is for Submit button for adding contract. --%>
<portlet:resourceURL var="addContractSubmit" id="addContractSubmit" escapeXml="false"></portlet:resourceURL>

<%-- This Portlet resource URL is for Find E-PIN button for getting contract details --%>
<portlet:resourceURL var='populateAddContractPage' id='populateAddContractPage' escapeXml='false'></portlet:resourceURL>
<portlet:resourceURL var="getProgramListForAgency" id="getProgramListForAgency"></portlet:resourceURL>
<portlet:resourceURL var='confirmFiscalYear' id='confirmFiscalYear' escapeXml='false'></portlet:resourceURL>
<input type="hidden" id="getProgramListForAgency" value="${getProgramListForAgency}"/>
<input type = 'hidden' value='${populateAddContractPage}' id='populateAddContractPageUrl'/>
<input type = 'hidden' value='${addContractSubmit}' id='addContractSubmitUrl'/>
<input type = 'hidden' value='${ConfigurableFiscalYears}' id='configurableFiscalYears'/>
<input type = 'hidden' value='${ConfigureFisalYearOverLay}' id='configureFisalYearOverLay'/>
<input type = 'hidden' value='${confirmFiscalYear}' id='confirmFiscalYearUrl'/>
<%-- Added fo Release 6 for new Epin format --%>
<input type = 'hidden' value='' id='typeAheadAgencyId'/>
<%-- Release 6 changes end --%>

<form:form id="addContractForm" action="${addContractSubmit}" method ="post" name="addContractForm">
<div class="content">
	<div id="newTabs">
		<div class='tabularCustomHead'>
			<span id="contractTypeId">Add Contract</span> 
			<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
		</div>
		
		<div id="addContract">
			<div class="tabularContainer">
			<%if(( null != request.getAttribute("Error") ) && !"".equalsIgnoreCase((String)request.getAttribute("Error"))){%>
				     <div id="transactionStatusDiv" class="failed breakAll" style="display:block" ><%=request.getAttribute("Error")%> </div>
				<%}%>
				 <div id="ErrorDiv" class="failed breakAll"> </div>
				<h2>Add Contract</h2>
				<div class='hr'></div>
				<p>
					To add a new contract, please search for the award E-PIN and click the 'Find E-PIN' button
				    to populate the information for the selected E-PIN. Then, populate all required fields,
				   update or add any missing information, and create the Contract by selecting the 'Add Contract' button.
				</p>
				<p><label class="required">*</label>Indicates a required field</p>
				<c:set var="epinBean" value="${epinBeanDetails}"></c:set>
				<%-- Release 6 : RefAptEpinId added for new Epin validation --%>
				<input type="hidden" id="refAptEpinId" name="refAptEpinId" value="${epinBean.refAptEpinId}"/>
				<%-- Release 6 changes end --%>
				<div class="formcontainer contractPopup">
					<div class="row">
						<div>
							<span class="label equalForms">
								<input id="epin" name="epin" type="text" value="${epinBean.awardEpin}" class='proposalConfigDrpdwn' maxlength="30" />
							</span> 
							<span class="formfield equalForms">
								<input id="searchepinbutton" name="searchepinbutton" type="button" class="button" value="Find E-PIN" 
									disabled="true" onclick="findEpin(document.getElementById('epin').value);"/>
							</span>
						</div>
						<div>
							<span class="error"></span>
						</div>
					</div>
				
				<div class="row">
					<span class="label equalForms"><label class="required">*</label>Award E-PIN:</span> 
					<span class="formfield equalForms">
						<input id="awardEpin" name="awardEpin" type="text" value="${epinBean.awardEpin}" readonly='readonly' class='proposalConfigDrpdwn' />
					</span>
					<span class="error"></span>
				</div>
				
			<div class="row">
				<span class="label equalForms">Procurement Start Date:</span> 
				<span class="formfield equalForms">
					<input id="procurementStartDate" name="procurementStartDate" type="text" value="${epinBean.procurementStartDate}" readonly='readonly' class='proposalConfigDrpdwn' /></span><span class="error">
				</span>
			</div>
		
			<div class='row'>
	            <span class="label equalForms"><label class="required">*</label>Agency:</span>
	            <span class="formfield equalForms">
	                  <select path="contractAgencyName" name="agencyId" id="agencyId" Class="proposalConfigDrpdwn">
	                 	<option id="All NYC Agencies" value=""></option>
	                  	<c:forEach items="${agencyDetails}" var="agencyDetail">
	                  		<option value="${agencyDetail['AGENCY_ID']}">${agencyDetail['AGENCY_NAME']}</option>
		                </c:forEach>
	                  </select>
	            </span>
	            <span class="error"></span>
	        </div>
				
			<div class="row">
				<span class="label equalForms">Agency Division:</span> 
				<span class="formfield equalForms">
					<input id="agencyDivision" name="agencyDivision" type="text" value="${epinBean.agencyDiv}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms">Agency ID:</span> 
				<span class="formfield equalForms">
					<input id="agencyIdFromAPT" name="agencyIdFromAPT" type="text" value="${epinBean.agencyId}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms">Award Agency ID:</span> 
				<span class="formfield equalForms">
					<input id="awardAgencyId" name="awardAgencyId" type="text" value="${epinBean.awardAgencyId}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms">Procurement Method:</span> 
				<span class="formfield equalForms">
					<input id="procurementMethod" name="procurementMethod" type="text" value="${epinBean.procMethod}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms">APT Project/Program:</span> 
				<span class="formfield equalForms">
					<input id="aptProject" name="aptProject" type="text" value="${epinBean.projProg}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
       
            <div class="row">
				<span class="label equalForms"><label class="required">*</label>Accelerator Program Name:</span> 
				<span class="formfield equalForms">
                  <select path="programName" name="accProgramName" id="accProgramName" Class="proposalConfigDrpdwn">
                  		<option value=""/>
                  		<c:forEach items="${programNameList}" var="programObject">
							<option value="${programObject.programId}">${programObject.programName}</option>
						</c:forEach>
					</select>
            	</span>
				<span class="error"></span>
        	</div>
    
			<div class="row">
				<span class="label equalForms"><label class="required">*</label>Contract Title (APT Procurement Desc.):</span> 
				<span class="formfield equalForms">
					<input id="contractTitlePopUp" name="contractTitlePopUp" type="text" value="${epinBean.procDescription}" class='proposalConfigDrpdwn' maxlength="120"/>
				</span>
				<span class="error"></span>
			</div>
				
			<div class="row">
				<span class="label equalForms"><label class="required">*</label>Awarded Vendor FMS ID:</span> 
				<span class="formfield equalForms">
					<input id="vendorFmsId" name="vendorFmsId" type="text" value="${epinBean.vendorFmsId}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
				<span class="error"></span>
			</div>
				
			<div class="row">
				<span class="label equalForms">Awarded Vendor FMS Name:</span> 
				<span class="formfield equalForms">
					<input id="vendorFmsName" name="vendorFmsName" type="text" value="${epinBean.vendorFmsName}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms">Accelerator Provider Legal Name:</span> 
				<span class="formfield equalForms">
					<input id="providerLegalName" name="providerLegalName" type="text" value="${epinBean.providerLegalName}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms"><label class="required">*</label>Contract Value (Award Amount) ($):</span> 
				<span class="formfield equalForms">
					<input id="contractValue" name="contractValue" type="text" value="${epinBean.contractValue}" class='proposalConfigDrpdwn' validate="number" />
				</span>
				<span class="error"></span>
			</div>
				
			<div class="row">
				<span class="label equalForms"><label class="required">*</label>Contract Start Date:</span> 
				<span class="formfield equalForms">
					<input id="contractStartDate" name="contractStartDate" type="text" value="${epinBean.contractStart}" class='proposalConfigDrpdwn date' validate="calender" maxlength="10" />
					<img src="../framework/skins/hhsa/images/calender.png" class="imgclassPlanned" onclick="NewCssCal('contractStartDate',event,'mmddyyyy');return false;">
				</span>
				<span class="error"></span>
			</div>
				
			<div class="row">
				<span class="label equalForms"><label class="required">*</label>Contract End Date:</span> 
				<span class="formfield equalForms">
					<input id="contractEndDate" name="contractEndDate" type="text" value="${epinBean.contractEnd}" validate="calender" maxlength="10" class='proposalConfigDrpdwn date' />	
					<img src="../framework/skins/hhsa/images/calender.png" class="imgclassPlanned" onclick="NewCssCal('contractEndDate',event,'mmddyyyy');return false;">
				</span>
				<span class="error"></span>
			</div>

			<div class="row">
				<span class="label clearLabel equalForms">
					<input name="chkContractCertFunds" type="checkbox" id="chkContractCertFunds" value="1" />
				</span>
				<span class="formfield equalForms">
					 <label for='chkContractCertFunds'>Certification of Funds Needed</label>
				</span>
			</div>
			
		</div>
			
		<div class="buttonholder">
			<input type="button" class="graybtutton" value="Cancel" onclick="clearAndCloseOverLay();"/> 
			<input type="submit" id="addContractButton"  class="button" value="Add Contract" />
		</div>
		
		</div>
		</div>
	</div>
</div>
<input type = 'hidden' value='' id='nextFiscalYearValue' name='nextFiscalYearValue' />
</form:form>
<%--below div block is added  as part of build 3.1.0, enhancement 6020  to show condition based  yes no pop up--%>
<%--Changes done for build 3.12.0, enhancement ID 6580 --%>
<div class="deleteOverlay"></div>
<div id="confirmFiscalYearDiv" class="alert-box-confirmFiscalYear">
            <div class="content">
                  <div id="newTabs" class='wizardTabs'>
                        <div class="tabularCustomHead">Configure Fiscal Year
                              <a href="javascript:void(0);" class="exitYesNo-panel" title="Exit"></a>
                        </div>
                        <div id="deleteDiv">
                        	 
                              <div class="pad6 clear promptActionMsg">Please select the Fiscal year you want to configure and Click on OK.
                              </div>
                              <div class="row">
                               <span class="label">Fiscal Year(FY):</span>
		  						<span class="formfield">
                                     <select id="fiscalYearId" name="fiscalYearId" Class="proposalConfigDrpdwn">
							           <option value="-1">Select FY</option> 
   									 </select>
   								</span>
                              </div>
                              <div class="buttonholder txtCenter">
                                    <input type="button" title="Cancel" class="graybtutton"  id="proceedCancel"  value="Cancel" onclick="closeCongifureFYOverlay();" />
                                    <input type="button" title="Ok" class="button" id="proceedOk" onclick="submitAddContract('ok');" value="OK" />
                              </div>
                        </div>
                  </div>
            </div>
</div>

<script type="text/javascript">
var requiredKey= "<fmt:message key='REQUIRED_FIELDS'/>";
</script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/addContract.js"></script>