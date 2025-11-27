<%@page import="java.util.*"%>
<%@page import="com.nyc.hhs.model.MissingNameBean"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="javax.portlet.PortletSession"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="auth" uri="http://www.bea.com/servers/p13n/tags/auth" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects/>
<style type="text/css">
	#newTabs h5{
		background:#E4E4E4;
		color: #5077AA;
	    font-size: 13px;
	    font-weight: bold;
	    padding: 6px 0px 6px 6px;
	}
	.alert-box{
		height: 300px;	   
	    margin-left:8%;
	    top: 25%;
	    width: 60%;	   
	}
	.alert-box .sub-started a{
		color:#fff;
	}	
	.alert-box .sub-started, .alert-box .sub-notstarted{
		background-image:none;
		color:#fff;
	}
</style>
<script type="text/javascript" src="../js/createadminacct.js"></script>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/address_validation.js"></script>


<%
MissingNameBean loMissingNameBean = new MissingNameBean();
if(null!=request.getAttribute("MissingNameBean")){
	loMissingNameBean = (MissingNameBean) request.getAttribute("MissingNameBean");
}
String  einNo = loMissingNameBean.getMsOrgEinTinNumber();
pageContext.setAttribute("missingNameBeanObj",loMissingNameBean);
%>



	<form id="createadminacct" action="<portlet:actionURL/>" method ="post" name="createadminacct">
		
		<c:set var="missingNameBean" value="${missingNameBeanObj}"></c:set>
		<input type="hidden" name="buttonHit" id="buttonHit" value="">
		<input type="hidden" name="StatusDescriptionTextReg" value=""/>
		<input type="hidden" name="StatusReasonReg" value=""/>
		<input type="hidden" name="StreetNumberTextReg" value=""/>
		<input type="hidden" name="CongressionalDistrictNameReg" value=""/>
		<input type="hidden" name="LatitudeReg" value=""/>
		<input type="hidden" name="LongitudeReg" value=""/>
		<input type="hidden" name="XCoordinateReg"  value=""/>
		<input type="hidden" name="YCoordinateReg"  value=""/>
		<input type="hidden" name="CommunityDistrictReg"  value=""/>
		<input type="hidden" name="CivilCourtDistrictReg"  value=""/>
		<input type="hidden" name="SchoolDistrictNameReg"  value=""/>
		<input type="hidden" name="HealthAreaReg"  value=""/>
		<input type="hidden" name="BuildingIdNumberReg"  value=""/>
		<input type="hidden" name="TaxBlockReg"  value=""/>
		<input type="hidden" name="TaxLotReg"  value=""/>
		<input type="hidden" name="SenatorialDistrictReg"  value=""/>
		<input type="hidden" name="AssemblyDistrictReg"  value=""/>
		<input type="hidden" name="CouncilDistrictReg"  value=""/>
		<input type="hidden" name="LowEndStreetNumberReg"  value=""/>
		<input type="hidden" name="HighEndStreetNumberReg"  value=""/>
		<input type="hidden" name="LowEndStreetNameReg"  value=""/>
		<input type="hidden" name="HighEndStreetNameReg"  value=""/>
		<input type="hidden" name="NYCBoroughReg"  value=""/>
		<input type="hidden" name="NormHouseNumberReg"  value=""/>
		
		<!-- Body Container Starts -->
		<div class="">
			<%
			String lsTransactionMsg = "";
			if (null!=request.getAttribute("transactionMessage")){
				lsTransactionMsg = (String)request.getAttribute("transactionMessage");
			}
			if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
			    <div id="transactionStatusDiv" class="failed" style="display:block" ><%=lsTransactionMsg%> </div>
			<%}%>
			<h2>Create Organization Account</h2>
			<div class='hr'></div>
			<div class="">
				<div class='formcontainer'>
					<p>To request access to the HHS-Accelerator system for your organization, please enter the information below and click the "Submit Account Request" button.</p>
					<p class='note'><span class='required'>*</span>Indicates required fields</p>	
				
					<h3>Organization Details</h3>
					<!-- Form Data Starts -->
				
					<div class="row" id="orgEinTinNoDiv">
					    <span class="label">Employee Identification Number/Tax Identification Number (EIN/TIN):</span>
					    <span class="formfield">
					      	<input name="orgEinTinNo" id="orgEinTinNo" type="text" class="input readonly" readonly="readonly" value="<%=loMissingNameBean.getMsOrgEinTinNumber()%>" />
					    </span>
					</div>
				 
					<div class="row" id="orgLegalNameDiv">
				    	<span class="label" title="Enter the legal name of your organization as it appears on the Certificate of Incorporation."><span class='required' >*</span>Organization Legal Name:</span>
				    	<span class="formfield"><input name="orgLegalName"   value="<%=loMissingNameBean.getMsOrgLegalName()%>" maxlength="100" id="orgLegalName" class="input "  type="text" /></span>
				    	<span class="error"></span>
					</div>
				
					<div class="row" id="orgCorpStrucDiv">
					    <span class="label"><span class='required'>*</span>Corporate Structure:</span>
					     <span class="formfield">
						      <select name="orgCorpStruc" id="orgCorpStruc"  class="input "     onchange="javascript:disableEntityType(this);">
							      <option value="">Select one</option>
							      <option value="For Profit" <% if("For Profit".equalsIgnoreCase(loMissingNameBean.getMsOrgCorpStructure())) {%>selected<%} %>>For Profit</option>
							      <option value="Non Profit" <% if("Non Profit".equalsIgnoreCase(loMissingNameBean.getMsOrgCorpStructure())) {%>selected<%} %>>Non Profit</option>
						      </select>
					    </span>
					    <span class="error"></span>
					</div>
				 
				
				 	 <!-- Sole Proprietor option for Release 3.10.0 . Enhancement 6572 -->
					<div style="display: none" class="row" id="entityTypeDiv" name="entityTypeDiv"> 
					    <span  class="label"><span class='required'>*</span>Entity Type:</span>
					    <span  class="formfield" >
							<select name="entityType" id="entityType"  class="input "     onchange="javascript:disableOthers(this);">
								<option value="">Select one</option>
								<option value="Corporation (any type)" <% if("Corporation (any type)".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>Corporation (any type)</option>
								<option value="LLC"<% if("LLC".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>LLC</option>
								<option value="Joint Venture" <% if("Joint Venture".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>Joint Venture</option>
								<option value="Partnership (any type)" <% if("Partnership (any type)".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>Partnership (any type)</option>
								<option value="Sole Proprietor" <% if("Sole Proprietor".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>Sole Proprietor</option>
								<option value="Other" <% if("Other".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>Other</option>
							</select>
					    </span>
					    <span class="error"></span>
					</div>
				 
				 
					<div class="row" style="display: none" id="othersDiv">
				    	<span class="label"><span class='required'>*</span>Other, Please Specify:</span>
				    	<span class="formfield"><input name="others" value="<%=loMissingNameBean.getMsOrgEntityTypeOther()%>" maxlength="150" id="others" class="input " type="text" /></span>
				    	<span class="error"></span>
					</div>
				 
			  		<div class="row" id="orgDunNoDiv">
				    	<span class="label" title="A Data Universal Numbering System (D-U-N-S number) is a unique nine digit identification number which is linked to each specific physical location of a business.  The number is assigned and maintained by Dun and Bradstreet (D&B).">Dun and Bradstreet Number (DUNS#):</span>
				    	<span class="formfield"><input name="orgDunNo" value="<%=loMissingNameBean.getMsOrgDunsNumber()%>" validate="number" maxlength="9" id="orgDunNo" class="input "     type="text" /></span>
				    	<span class="error"></span>
			  		</div>
				  
			  		<div class="row" id="orgAltName">
				    	<span class="label" title="Doing Business As (DBA) or Alternate Name refers to a commonly used and registered name for the organization which differs from its legal name.Only complete this field if a Certificate of Assumed Name or equivalent document has been filed.">Doing Business As (DBA) or Alternate Name:</span>
				    	<span class="formfield"><input name="orgAltName" value="<%=loMissingNameBean.getMsOrgDoingBusAs()%>" maxlength="100" id="orgAltName" class="input "     type="text" /></span>
				    	<span class="error"></span>
			  		</div>
				  
			  		<div class="row" id="acctPeriodDiv">
				    <span class="label" title="Refers to the first month of the organization’s fiscal year" ><span class='required'>*</span>Accounting Period:</span>
					    <span class="formfield">
							<select name="acctPeriodFrom" id="acctPeriodFrom"  class="input "  style="width:70px" >
							    <option title="Jan" value="Jan" <% if("Jan".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>Jan</option>
							    <option title="Feb" value="Feb" <% if("Feb".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>Feb</option>
								<option title="Mar" value="Mar" <% if("Mar".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>Mar</option>
								<option title="Apr" value="Apr" <% if("Apr".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>Apr</option>
							    <option title="May" value="May" <% if("May".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>May</option>
							    <option title="Jun" value="Jun" <% if("Jun".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>Jun</option>
								<option title="Jul" value="Jul" <% if("Jul".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>Jul</option>
							    <option title="Aug" value="Aug" <% if("Aug".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>Aug</option>
								<option title="Sep" value="Sep" <% if("Sep".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>Sep</option>
							    <option title="Oct" value="Oct" <% if("Oct".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>Oct</option>
							    <option title="Nov" value="Nov" <% if("Nov".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>Nov</option>
							    <option title="Dec" value="Dec" <% if("Dec".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodStart())) {%>selected<%} %>>Dec</option>
							 </select> <span title="Refers to the last month of the organization’s fiscal year">&nbsp; to &nbsp;</span>
						 	 <select name="acctPeriodTo"  id="acctPeriodTo"  class="input "  style="width:70px" >
							    <option title="Jan" value="Jan" <% if("Jan".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>Jan</option>
							    <option title="Feb" value="Feb" <% if("Feb".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>Feb</option>
							    <option title="Mar" value="Mar" <% if("Mar".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>Mar</option>
							    <option title="Apr" value="Apr" <% if("Apr".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>Apr</option>
							    <option title="May" value="May" <% if("May".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>May</option>
							    <option title="Jun" value="Jun" <% if("Jun".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>Jun</option>
							    <option title="Jul" value="Jul" <% if("Jul".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>Jul</option>
							    <option title="Aug" value="Aug" <% if("Aug".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>Aug</option>
							    <option title="Sep" value="Sep" <% if("Sep".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>Sep</option>
							    <option title="Oct" value="Oct" <% if("Oct".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>Oct</option>
							    <option title="Nov" value="Nov" <% if("Nov".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>Nov</option>
							    <option title="Dec" value="Dec" <% if("Dec".equalsIgnoreCase(loMissingNameBean.getMsOrgAcctPeriodEnd())) {%>selected<%} %>>Dec</option>          
							</select>
					    </span>
					</div>
				   
					<div class='hr'></div>
			  		<h3>Executive Office  Address</h3>
				 
			  		<div class="row" id="execAddLine1Div">
				    	<span class="label"><span class='required'>*</span>Address Line 1:</span>
				    	<span class="formfield"><input name="execAddLine1" value="<%=loMissingNameBean.getMsExecAddrLine1()%>" id="execAddLine1" class="input" maxlength="60" type="text" /></span>
				    	<span class="error"></span>
			  		</div>
				 
					<div class="row" id="execAddLine2Div">
					    <span class="label">Address Line 2:</span>
					    <span class="formfield">
					    	<input  name="execAddLine2" id="execAddLine2" value="<%=loMissingNameBean.getMsExecAddrLine2()%>" class="input" maxlength="60" type="text" />
					    </span>
					    <span class="error"></span>
					</div>
				    
			  		<div class="row" id="execCityDiv">
					    <span class="label"><span class='required'>*</span>City:</span>
					    <span class="formfield">
					    	<input  name="execCity" id="execCity" value="<%=loMissingNameBean.getMsExecCity()%>" class="input "  maxlength="40"  type="text" />
					    </span>
					    <span class="error"></span>
			  		</div>
				     
				    
					<div class="row" id="execStateDiv">
					    <span class="label"><span class='required'>*</span>State:</span>
					    <span class="formfield">
							<select name="execState" id="execState"  class="input "     name="select2">
								<option value="">Select one</option>
								<c:forEach var="memberTitle" items="${memberState}">
									<option value="${memberTitle.key}" <c:if test="${memberTitle.key==missingNameBean.msExecState}">selected</c:if>>${memberTitle.value}</option>
								</c:forEach>
							</select>
					    </span>
					    <span class="error"></span>
					</div>
				   
					<div class="row" id="execZipCodeDiv">
					    <span class="label"><span class='required'>*</span>Zipcode:</span>
					    <span class="formfield">
					    	<input  name="execZipCode" value="<%=loMissingNameBean.getMsExecZipCode()%>" id="execZipCode" class="input " type="text" />
					    </span>
					    <span class="error"></span>
					</div>
				    
					<div class="row" id="execPhNoDiv">
					    <span class="label"><span class='required'>*</span>Phone Number:</span>
					    <span class="formfield">
					    	<input name="execPhNo" value="<%=loMissingNameBean.getMsExecPhoneNo()%>" id="execPhNo" class="input " type="text" >
					    </span>
					    <span class="error"></span>
					</div>
				   
					<div class="row" id="execFaxNoDiv">
					    <span class="label">Fax Number:</span>
					    <span class="formfield">
					    	<input name="execFaxNo" value="<%=loMissingNameBean.getMsExecFaxNo()%>" id="execFaxNo" class="input " type="text"></span> 
				    </div>
				   
					<div class="row" id="execWebSiteDiv">
					    <span class="label">Website:</span>
					    <span class="formfield">
					    	<input name="execWebSite" value="<%=loMissingNameBean.getMsExecWebSite()%>" maxlength="60" id="execWebSite" class="input " type="text" />
					    </span>
				    </div>
				    
					<div class='hr'></div>
				 
					<h3>Account Administrator Details</h3>
				
					<div class="row" id="nycIdDiv">
					    <span class="label">NYC ID:</span>
					    <span class="formfield">
					    	<input name="nycId" id="nycId"  maxlength="128" type="text"  class="input readonly" readonly="readonly"  value="<%=loMissingNameBean.getMsAdminNYCId()%>" />
					    </span>
				    </div>
				
					<div class="row" id="adminFirstNameDiv">
					    <span class="label">First Name:</span>
					    <span class="formfield">
					    	<input name="adminFirstName" id="adminFirstName" type="text"  class="input readonly" value="<%=loMissingNameBean.getMsAdminFirstName()%>"  readonly="readonly" />
					    </span>
				 	</div>
				 	
					<div class="row" id="adminMiddleNameDiv">
				    	<span class="label">Middle Initial:</span>
				    	<span class="formfield"><input name="adminMiddleName"  id="adminMiddleName"  class="input readonly" type="text" value="<%=loMissingNameBean.getMsAdminMiddleInitial()%>" readonly="readonly"  /></span>
				  	</div>
				  	
				    <div class="row" id="adminLastNameDiv">
				    	<span class="label">Last Name:</span>
				    	<span class="formfield"><input name="adminLastName"  id="adminLastName" class="input readonly "     type="text" value="<%=loMissingNameBean.getMsAdminLastName()%>"   readonly="readonly" /></span>
				 	</div>
				 
				 	<div class="row" id="adminOfficeTitleDiv">
					    <span class="label"><span class='required'>*</span>Office Title:</span>
					    <span class="formfield">
						    <select name="adminOfficeTitle" id="adminOfficeTitle"  class="input "     >
							    <option value="" >Select one</option>
							    <c:forEach var="adminTitle" items="${adminOffTitle}">
									<option value="${adminTitle.key}" <c:if test="${adminTitle.key==missingNameBean.msAdminOfficeTitle}">selected</c:if>>${adminTitle.value}</option>
								</c:forEach>
							</select>
					    </span>
					    <span class="error"></span>
				    </div>
				    
				 	<div class="row" id="adminPhNoDiv">
					    <span class="label"><span class='required'>*</span>Phone Number:</span>
					    <span class="formfield">
					    	<input name="adminPhNo" value="<%=loMissingNameBean.getMsAdminPhoneNo()%>" id="adminPhNo" class="input " type="text">
					    </span>
					    <span class="error"></span> 
				 	</div>
				 	
				    <div class="row" id="adminEmailDiv">
					    <span class="label">Email Address:</span>
					    <span class="formfield"><input name="adminEmail"  id="adminEmail" class="input " class="input readonly"   type="text" value="<%=loMissingNameBean.getMsAdminEmailAdd()%>"  readonly="readonly"/></span>
				    </div>
				    
				    <div class='hr'></div>
				
					<h3>Chief Executive Officer / Executive Director (or equivalent)</h3>
					
					<div class="row">
						<span class="label" ></span>
						<span class="formfield"><input id="copyAdminInfoForCeo" name="copyAdminInfoForCeo" type="checkbox" onclick="javascript:copyAdminInformationForCEO();"/>Use Administrator Information</span>					
					</div>
				
					<div class="row" id="ceoFirstNameDiv">
					    <span class="label"><span class='required'>*</span>First Name:</span>
					    <span class="formfield">
					    	<input name="ceoFirstName" value="<%=loMissingNameBean.getMsCeoFirstName()%>" maxlength="32" id="ceoFirstName" type="text" class="input " value="" />
					    </span>
					    <span class="error"></span>
				 	</div>
				 	
				  	<div class="row" id="ceoMiddleNameDiv">
					    <span class="label">Middle Initial:</span>
					    <span class="formfield"><input name="ceoMiddleName" value="<%=loMissingNameBean.getMsCeoMiddleInitial()%>" maxlength="1" id="ceoMiddleName" class="input " type="text" style='width:36px;' /></span>
				  	</div>
				  	
				    <div class="row" id="ceoLastNameDiv">
					    <span class="label"><span class='required'>*</span>Last Name:</span>
					    <span class="formfield"><input name="ceoLastName" value="<%=loMissingNameBean.getMsCeoLastName()%>" maxlength="64" id="ceoLastName" class="input " type="text" /></span>
					    <span class="error"></span>
				 	</div>
				 	
				 	<div class="row" id="ceoPhNoDiv">
					    <span class="label"><span class='required'>*</span>Phone Number:</span>
					    <span class="formfield"><input name="ceoPhNo" value="<%=loMissingNameBean.getMsCeoPhoneNo()%>" id="ceoPhNo" class="input " type="text"></span>
					    <span class="error"></span> 
					</div>
					
				  	<div class="row" id="ceoEmailDiv">
					    <span class="label"><span class='required'>*</span>Email Address:</span>
					    <span class="formfield"><input name="ceoEmail" value="<%=loMissingNameBean.getMsCeoEmailAdd()%>" id="ceoEmail" maxlength="128" class="input " type="text" /></span>
					    <span class="error"></span>
				    </div>
				    
					<div class='hr'></div>
				
					<h3>Chief Financial Officer (or equivalent)</h3>
				
					<div class="row" id="isCFODiv">
						<span class="label"></span><span>My organization has a CFO:</span>
					
						<input id="cfoYes" name="isCfo" type="radio" class="radio-btn" value="Yes" onclick="javascript:enableCFOFields();"/> Yes
						<input id="cfoNo" checked  name="isCfo" type="radio" class="radio-btn" value="No"  onclick="javascript:disableCFOFields();"/> No
					
					
						</span>
					</div>
					
					<div class="row" id="copyCFO" style="display: none">
						<span class="label" ></span>
						<span class="formfield"><input id="copyAdminInfoForCfo" name="copyAdminInfoForCfo" type="checkbox" onclick="javascript:copyAdminInformationForCFO();"/>Use Administrator Information</span>					
					</div>
					
					<div class="row" id="cfoFirstNameDiv" style="display: none">
					    <span class="label"><span class='required'>*</span>First Name:</span>
					    <span class="formfield">
					    	<input name="cfoFirstName" value="<%=loMissingNameBean.getMsCfoFirstName()%>" maxlength="32" id="cfoFirstName" type="text" class="input " value="" />
					    </span>
					    <span class="error"></span>
				 	</div>
				 	
				  	<div class="row" id="cfoMiddleNameDiv" style="display: none">
					    <span class="label">Middle Initial:</span>
					    <span class="formfield"><input name="cfoMiddleName" value="<%=loMissingNameBean.getMsCfoMiddleInitial()%>" maxlength="1" id="cfoMiddleName" class="input " type="text" style='width:36px;' /></span>
				    </div>
				  
				    <div class="row" id="cfoLastNameDiv" style="display: none">
					    <span class="label"><span class='required'>*</span>Last Name:</span>
					    <span class="formfield"><input name="cfoLastName" value="<%=loMissingNameBean.getMsCfoLastName()%>" maxlength="64" id="cfoLastName" class="input " type="text" /></span>
					    <span class="error"></span>
				 	</div>
				 	
				 	<div class="row" id="cfoPhNoDiv" style="display: none">
					    <span class="label"><span class='required'>*</span>Phone Number:</span>
					    <span class="formfield"><input name="cfoPhNo" value="<%=loMissingNameBean.getMsCfoPhoneNo()%>" id="cfoPhNo" class="input " type="text"></span>
					    <span class="error"></span> 
					</div>
					
				  	<div class="row" id="cfoEmailDiv" style="display: none">
					    <span class="label"><span class='required'>*</span>Email Address:</span>
					    <span class="formfield"><input name="cfoEmail" value="<%=loMissingNameBean.getMsCfoEmailAdd()%>" id="cfoEmail" maxlength="128" class="input " type="text" /></span>
					    <span class="error"></span>
				    </div>
				    
					<div class='hr'></div>
				
					<h3>Board Chair / President</h3>
					
					<div class="row">
						<span class="label" ></span>
						<span class="formfield"><input id="copyAdminInfoForPres" name="copyAdminInfoForPres" type="checkbox" onclick="javascript:copyAdminInformationForPresident();"/>Use Administrator Information</span>					
					</div>
					
					<div class="row" id="presFirstNameDiv">
					    <span class="label"><span class='required'>*</span>First Name:</span>
					    <span class="formfield">
					    	<input name="presFirstName" maxlength="32" value="<%=loMissingNameBean.getMsPresFirstName()%>" id="presFirstName" type="text" class="input " value="" />
					    </span>
					    <span class="error"></span>
				 	</div>
				 	
				  	<div class="row" id="presMiddleNameDiv">
					    <span class="label">Middle Initial:</span>
					    <span class="formfield"><input name="presMiddleName" value="<%=loMissingNameBean.getMsPresMiddleInitial()%>" maxlength="1" id="presMiddleName" class="input " type="text" style='width:36px;' />
					    </span>
				  	</div>
				  	
				    <div class="row" id="presLastNameDiv">
					    <span class="label"><span class='required'>*</span>Last Name:</span>
					    <span class="formfield"><input name="presLastName" value="<%=loMissingNameBean.getMsPresLastName()%>" maxlength="64" id="presLastName" class="input " type="text" />
					    </span>
					    <span class="error"></span>
				 	</div>
				 	
				 	<div class="row" id="presPhNoDiv">
					    <span class="label"><span class='required'>*</span>Phone Number:</span>
					    <span class="formfield"><input name="presPhNo" value="<%=loMissingNameBean.getMsPresPhoneNo()%>" id="presPhNo" class="input "     type="text"/></span>
					    <span class="error"></span>
					</div>
					
				  	<div class="row" id="presEmailDiv">
					    <span class="label"><span class='required'>*</span>Email Address:</span>
					    <span class="formfield"><input name="presEmail" value="<%=loMissingNameBean.getMsPresEmailAdd()%>"" id="presEmail" maxlength="128" class="input " type="text" /></span>
					    <span class="error"></span>
				    </div>
				
					<div style="background: url(https://mtdlvw-hhs-acc1.csc.nycnet:8444/HHSPortal/framework/skins/hhsa/images/info-icon1.png) no-repeat 1px 3px;background-color: lightblue;padding: 7px 2px 7px 33px;"> After clicking the "Submit Account Request" button, no further actions are required on this page. An email notification will be sent to you once a decision has been made by MOCS regarding your request.</div>
					<div class="buttonholder"><input name="sbmtAcctReq" id="sbmtAcctReq" type="submit" class="button" title="Submit Account Request" value="Submit Account Request"></div>
					<!-- Form Data Ends -->
				</div>
			</div>
		</div>
		
		<!-- Body Container Ends -->
		
	 </form>
	 

<script language="javascript">
var contextPathVariable = "<%=request.getContextPath()%>";
var popupWindow = null;
function centeredPopup(url,winName,w,h,scroll){
	LeftPosition = (screen.width) ? (screen.width-w)/2 : 0;
	TopPosition = (screen.height) ? (screen.height-h)/2 : 0;
	settings =
	'height='+h+',width='+w+',top='+TopPosition+',left='+LeftPosition+',scrollbars='+scroll+',resizable';
	popupWindow = window.open(url,winName,settings);
}

//jquery ready function- executes the script after page loading
$(document).ready(function() {
		$('#ceoFirstName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
		$('#ceoMiddleName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
		$('#ceoLastName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});

		$('#cfoFirstName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
		$('#cfoMiddleName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
		$('#cfoLastName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});

		$('#presFirstName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
		$('#presMiddleName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});
		$('#presLastName').alphanumeric( { allow: "-,.' \\\"" , nchars:"_0123456789"});

	if($("#acctPeriodFrom").prop("selectedIndex") == 0){
		$("#acctPeriodTo option").eq(11).attr('selected', 'selected');
	}
	$("#acctPeriodFrom").change(function(){
		var selectedIndex = $(this).prop("selectedIndex");
		var toSet = selectedIndex - 1;
		if(toSet == -1)
			toSet = 11;
		$("#acctPeriodTo option").eq(toSet).attr('selected', 'selected');
	});
	$("#acctPeriodTo").change(function(){
		var selectedIndex = $(this).prop("selectedIndex");
		var toSet = selectedIndex + 1;
		if(toSet == 12)
			toSet = 0;
		$("#acctPeriodFrom option").eq(toSet).attr('selected', 'selected');
	});
	
	
// jquery validation on submit button click	
	$("#sbmtAcctReq").click(function() {

if(document.getElementById("copyAdminInfoForCeo").checked && (null==document.getElementById("ceoPhNo").value) || document.getElementById("ceoPhNo").value=='' ){
	document.getElementById("ceoPhNo").value=document.getElementById("adminPhNo").value;
}
if(document.getElementById("copyAdminInfoForCfo").checked && (null==document.getElementById("cfoPhNo"))|| document.getElementById("cfoPhNo").value==''){
	document.getElementById("cfoPhNo").value=document.getElementById("adminPhNo").value;
}
if(document.getElementById("copyAdminInfoForPres").checked && (null==document.getElementById("presPhNo")) || document.getElementById("presPhNo").value==''){
	document.getElementById("presPhNo").value=document.getElementById("adminPhNo").value;
}
		$("#createadminacct").validate({

			rules : {

				orgLegalName : {
					required : true,
					minlength : 3,
					allowSpecialChar: ["A", " !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7"]
				},

				orgCorpStruc : {
					required : true
				},

				others : {
					required : true,
					allowSpecialChar: ["A", " !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7"]
				},

				entityType : {
					required : true
				},

				orgDunNo : {
					minlength : 9,
					allowSpecialChar: ["N", " !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7"]
				},
				
				orgAltName : {
					allowSpecialChar: ["A", " !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7"]
				},
				acctPeriodFrom : {
					required : true
				},

				acctPeriodTo : {
					required : true
				},

				execAddLine1 : {
					required : true, allowSpecialChar: ["A", ",.\\\"\\\'\\\# -"] 
				},
				
				execAddLine2 : {
					allowSpecialChar: ["A", ",.\\\"\\\'\\\# -"] 
				},

				execCity : {
					required : true, allowSpecialChar: ["T", ",.\\\"\\\'\\\ -"]
				},

				execState : {
					required : true
				},

				execZipCode : {
					required : true

				},

				execPhNo : {
					required : true,
					minlength : 12
				},
				execWebSite : {
					allowSpecialChar: ["A", " !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7"]
				},
				adminOfficeTitle : {
					required : true
				},

				adminPhNo : {
					required : true,
					minlength : 12
				},

				ceoFirstName : {
					required : true, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
				},

				ceoMiddleName : {
					allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
				},

				ceoLastName : {
					required : true, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
				},

				ceoPhNo : {
					required : true,
					minlength : 12
				},

				ceoEmail : {
					required : true,
					email : true
				},

				cfoFirstName : {
					required : true, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
				},

				cfoMiddleName : {
					allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
				},

				cfoLastName : {
					required : true, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
				},

				cfoPhNo : {
					required : true,
					minlength : 12
				},

				cfoEmail : {
					required : true,
					email : true

				},
				presFirstName : {
					required : true, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
				},

				presMiddleName : {
					allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
				},
				
				presLastName : {
					required : true, allowSpecialChar: ["T", ",.\\\"\\\'\\\" -"]
				},

				presPhNo : {
					required : true,
					minlength : 12
				},

				presEmail : {
					required : true,
					email : true

				}
			},
			messages : {
				orgLegalName : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					minlength : "<fmt:message key='ORG_LEGAL_NAME_MIN_LENGTH'/>",
					allowSpecialChar: "! Only ! @ # $ % ^ & * () - _ + = | \ { } [ ] ; : \" \' < > , . ? / ` ~ \xA7 and \n spaces are allowed as a special character."
				},
				orgCorpStruc : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>"
				},
				others : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					allowSpecialChar: "! Only ! @ # $ % ^ & * () - _ + = | \ { } [ ] ; : \" \' < > , . ? / ` ~ \xA7 and \n spaces are allowed as a special character."
				},
				entityType : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>"
				},
				orgDunNo : {
					required : "<fmt:message key='DUSN_MIN_LENGTH'/>",
					minlength : "<fmt:message key='ORG_DUN_MIN_LENGTH'/>",
					allowSpecialChar: "! ! Only numeric values allowed."
				},
				orgAltName : {
					allowSpecialChar: "! Only ! @ # $ % ^ & * () - _ + = | \ { } [ ] ; : \" \' < > , . ? / ` ~ \xA7 and \n spaces are allowed as a special character."
				},
				acctPeriodFrom : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>"
				},
				acctPeriodTo : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>"
				},
				execAddLine1 : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_ADDRESS'/>" 
				},
				execAddLine2 : {
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_ADDRESS'/>" 
				},
				execCity : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_CITY'/>"
				},
				execState : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>"
				},
				execZipCode : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>"
				},
				execWebSite : {
					allowSpecialChar: "! Only ! @ # $ % ^ & * () - _ + = | \ { } [ ] ; : \" \' < > , . ? / ` ~ \xA7 and \n spaces are allowed as a special character."
				},
				execPhNo : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					minlength : "<fmt:message key='PHONE_NO_MIN_LENGTH'/>"
				},
				adminOfficeTitle : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>"
				},
				adminPhNo : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					minlength : "<fmt:message key='PHONE_NO_MIN_LENGTH'/>"
				},
				ceoFirstName : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
				},
				ceoMiddleName : {
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
				},
				ceoLastName : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
				},
				ceoPhNo : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					minlength : "<fmt:message key='PHONE_NO_MIN_LENGTH'/>"
				},
				ceoEmail : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					email : "<fmt:message key='EMAIL_REQUIREMENT'/>"
				},
				cfoFirstName : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
				},
				cfoMiddleName : {
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
				},
				cfoLastName : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
				},
				cfoPhNo : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					minlength : "<fmt:message key='PHONE_NO_MIN_LENGTH'/>"
				},
				cfoEmail : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					email : "<fmt:message key='EMAIL_REQUIREMENT'/>"
				},
				presFirstName : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
				},
				presMiddleName : {
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
				},
				presLastName : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_NAME'/>"
				},
				presPhNo : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					minlength : "<fmt:message key='PHONE_NO_MIN_LENGTH'/>"
				},
				presEmail : {
					required : "<fmt:message key='REQUIRED_FIELDS'/>",
					email : "<fmt:message key='EMAIL_REQUIREMENT'/>"
				}

			},
			// address validation by submit handler
			submitHandler : function(form) {
				var address1 = document.getElementById("execAddLine1").value;
				var city = document.getElementById("execCity").value;
				var state = document.getElementById("execState").value;
				var zipcode = document.getElementById("execZipCode").value;
				pageGreyOut();
				validateAddress(address1, city, state, zipcode);
				return false;
			}
			,errorPlacement: function(error, element) {
             error.appendTo(element.parent().parent().find("span.error"));}

		});
	});

// address validation that includes validation of address, city, state and zipcode
	function validateAddress(address1, city, state, zipcode) {
		jQuery
				.ajax({
					type : "POST",
					url : contextPathVariable+"/AddressValidationServlet.jsp?address1="
							+ escape(address1) + "&city=" + escape(city) + "&state=" + escape(state)
							+ "&zipcode=" + escape(zipcode),
					data : "",
					success : function(e) {
						removePageGreyOut();
					if(e.indexOf("byPassValidation")>-1){
						$("#addressDiv").empty();
						$("#addressDiv").html(e);
					 var selectedRadio = $(".rdoBtn:checked")
						.parent().parent();
					 $("input[name='StatusDescriptionTextReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='StatusDescriptionText']")
										.val());
						$("input[name='StatusReasonReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='StatusReason']")
										.val());
						$("input[name='NormHouseNumberReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='StreetNumberText']")
										.val());
						$("input[name='StreetNumberTextReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='newAddress']")
										.val());
						$("input[name='CongressionalDistrictNameReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='CongressionalDistrictName']")
										.val());
						$("input[name='LatitudeReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='Latitude']")
										.val());
						$("input[name='LongitudeReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='Longitude']")
										.val());
						$("input[name='XCoordinateReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='XCoordinate']")
										.val());
						
						$("input[name='YCoordinateReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='YCoordinate']")
										.val());
						
						$("input[name='CommunityDistrictReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='CommunityDistrict']")
										.val());
						
						$("input[name='CivilCourtDistrictReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='CivilCourtDistrict']")
										.val());
						
						$("input[name='SchoolDistrictNameReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='SchoolDistrictName']")
										.val());
						$("input[name='HealthAreaReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='HealthArea']")
										.val());
						$("input[name='BuildingIdNumberReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='BuildingIdNumber']")
										.val());
						$("input[name='TaxBlockReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='TaxBlock']")
										.val());
						$("input[name='TaxLotReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='TaxLot']")
										.val());
						$("input[name='SenatorialDistrictReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='SenatorialDistrict']")
										.val());
						$("input[name='AssemblyDistrictReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='AssemblyDistrict']")
										.val());
						$("input[name='CouncilDistrictReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='CouncilDistrict']")
										.val());
						$("input[name='LowEndStreetNumberReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='LowEndStreetNumber']")
										.val());
						$("input[name='HighEndStreetNumberReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='HighEndStreetNumber']")
										.val());
						$("input[name='LowEndStreetNameReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='LowEndStreetName']")
										.val());
						$("input[name='HighEndStreetNameReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='HighEndStreetName']")
										.val());
						$("input[name='NYCBoroughReg']")
						.val(
								selectedRadio
										.find(
												"input[type='hidden'][name='NYCBorough']")
										.val());
				
						document.createadminacct.action = document.createadminacct.action
															+ '&next_action=orgAcctRequestSubmitted&accoutRequestmodule=accoutRequestmodule';
					    document.createadminacct.submit();
					}else {
						$("#addressDiv").empty();
						$("#addressDiv").html(e);
						$(".overlay").launchOverlay($(".alert-box-address"),
								$(".exit-panel"));
						//removePageGreyOut();
						$(".alert-box-address")
								.find('#selectaddress')
								.click(
										
										function() {
											pageGreyOut();
											var selectedRadio = $(".rdoBtn:checked")
													.parent().parent();
											$("input[name='execAddLine1']")
													.val(
															selectedRadio
																	.find(
																			"input[type='hidden'][name='newAddress']")
																	.val());
											$("input[name='execCity']")
													.val(
															selectedRadio
																	.find(
																			"input[type='hidden'][name='newCity']")
																	.val());
											$("input[name='execZipCode']")
													.val(
															selectedRadio
																	.find(
																			"input[type='hidden'][name='newZip']")
																	.val());
											$(
													"select[name='execState']>option[value='"
															+ selectedRadio
																	.find(
																			"input[type='hidden'][name='newState']")
																	.val() + "']")
													.attr('selected', 'selected');
											
											$("input[name='StatusDescriptionTextReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='StatusDescriptionText']")
															.val());
											$("input[name='StatusReasonReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='StatusReason']")
															.val());
											$("input[name='NormHouseNumberReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='StreetNumberText']")
															.val());
											$("input[name='StreetNumberTextReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='newAddress']")
															.val());
											$("input[name='CongressionalDistrictNameReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='CongressionalDistrictName']")
															.val());
											$("input[name='LatitudeReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='Latitude']")
															.val());
											$("input[name='LongitudeReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='Longitude']")
															.val());
											$("input[name='XCoordinateReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='XCoordinate']")
															.val());
											
											$("input[name='YCoordinateReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='YCoordinate']")
															.val());
											
											$("input[name='CommunityDistrictReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='CommunityDistrict']")
															.val());
											
											$("input[name='CivilCourtDistrictReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='CivilCourtDistrict']")
															.val());
											
											$("input[name='SchoolDistrictNameReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='SchoolDistrictName']")
															.val());
											$("input[name='HealthAreaReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='HealthArea']")
															.val());
											$("input[name='BuildingIdNumberReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='BuildingIdNumber']")
															.val());
											$("input[name='TaxBlockReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='TaxBlock']")
															.val());
											$("input[name='TaxLotReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='TaxLot']")
															.val());
											$("input[name='SenatorialDistrictReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='SenatorialDistrict']")
															.val());
											$("input[name='AssemblyDistrictReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='AssemblyDistrict']")
															.val());
											$("input[name='CouncilDistrictReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='CouncilDistrict']")
															.val());
											$("input[name='LowEndStreetNumberReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='LowEndStreetNumber']")
															.val());
											$("input[name='HighEndStreetNumberReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='HighEndStreetNumber']")
															.val());
											$("input[name='LowEndStreetNameReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='LowEndStreetName']")
															.val());
											$("input[name='HighEndStreetNameReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='HighEndStreetName']")
															.val());
											$("input[name='NYCBoroughReg']")
											.val(
													selectedRadio
															.find(
																	"input[type='hidden'][name='NYCBorough']")
															.val());
											$(".overlay").closeOverlay();
											
											document.createadminacct.action = document.createadminacct.action
													+ '&next_action=orgAcctRequestSubmitted&accoutRequestmodule=accoutRequestmodule';
											document.createadminacct.submit();
											// submitForm(buttonName);
											return false;
										});
						}
					},
					beforeSend : function() { //function for loading wheel
					}
				});
	}	
});
</script>



<div class="overlay"></div>
<div class="alert-box alert-box-address">
	<div class="content">
		<div id="newTabs" class='wizardTabs'>
			<div class="tabularCustomHead">Address Validation</div>
			<div id="addressDiv"></div>
		</div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>

