<%@page import="java.util.*"%>
<%@page import="com.nyc.hhs.model.MissingNameBean"%>
<%@page import="javax.portlet.PortletSession"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="auth" uri="http://www.bea.com/servers/p13n/tags/auth" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects/>

<style type="text/css">

.alert-box
{
	background: none repeat scroll 0 0 #FFFFFF;
    display: none;
    height: 300px;
    position: fixed;
   /* right: 25%; */
   margin-left:8%;
    top: 25%;
    width: 60%;
    z-index: 1001;
}

a.exit-panel
{ 
	position: absolute; 
	right: -4px; 
	top: -4px; 
	height: 39px; 
	width: 40px; 
	z-index:1002; 
	background:url(images/iconClose.png) no-repeat center center;
	
}
#newTabs{
	
}
#newTabs h5{
	background:#E4E4E4;
	color: #5077AA;
    font-size: 13px;
    font-weight: bold;
    padding: 6px 0px 6px 6px;
}
.alert-box .sub-started a{
	color:#fff;
}
.alert-box .sub-started, .alert-box .sub-notstarted{
	background-image:none;
	color:#fff;
}
</style>

<script language="javascript">

	var popupWindow = null;
	function centeredPopup(url,winName,w,h,scroll){
		LeftPosition = (screen.width) ? (screen.width-w)/2 : 0;
		TopPosition = (screen.height) ? (screen.height-h)/2 : 0;
		settings =
		'height='+h+',width='+w+',top='+TopPosition+',left='+LeftPosition+',scrollbars='+scroll+',resizable';
		popupWindow = window.open(url,winName,settings);
	}

</script>




<%
MissingNameBean loMissingNameBean = new MissingNameBean();
if(null!=request.getAttribute("MissingNameBean")){
	loMissingNameBean = (MissingNameBean) request.getAttribute("MissingNameBean");
}
String  einNo = loMissingNameBean.getMsOrgEinTinNumber();
pageContext.setAttribute("missingNameBeanObj",loMissingNameBean);
%>

<!--<script type="text/JavaScript" src="js/curvycorners.js"></script> -->

<title>NYC_Human Health Services Accelerator</title>
<form id="createadminacct" action="<portlet:actionURL/>" method ="post" name="createadminacct">
	
	<c:set var="missingNameBean" value="${missingNameBeanObj}"></c:set>
	<input type="hidden" name="buttonHit" id="buttonHit" value="">
	
	<!-- Body Container Starts -->
	<div class="">
		<h2>Create Organization Account</h2>
		<div class='hr'></div>
		<div class="">
			<div class='formcontainer'>
			<!-- <p>To request access to the HHS-Accelerator system for your organization, please enter the information below and click the "Submit Account Request" button.</p> -->
			<!-- 	<p class='note'><span class='required'>*</span>Indicates required fields</p>  -->	
			
			<h3>Organization Details</h3>
			<!-- Form Data Starts -->
			
			<div class="row" id="orgEinTinNoDiv">
				 <span class="label">Employee Identification Number/Tax Identification Number (EIN/TIN):</span>
				 <span class="formfield">
			     	<input name="orgEinTinNo" id="orgEinTinNo" type="text" class="input readonly" readonly="readonly"  value="<%=loMissingNameBean.getMsOrgEinTinNumber()%>" />
			     </span>
			 </div>
			 
			 <div class="row" id="orgLegalNameDiv">
			 	<span class="label"><span class='required'>*</span>Organization Legal Name:</span>
			 	<span class="formfield"><input name="orgLegalName" value="<%=loMissingNameBean.getMsOrgLegalName()%>" maxlength="100" id="orgLegalName" class="input readonly"   readonly="readonly"  type="text" /></span>
			</div>
			
			<div class="row" id="orgCorpStrucDiv">
				<span class="label"><span class='required'>*</span>Corporate Structure:</span>
			    <span class="formfield">
			    	<select name="orgCorpStruc" value="<%=loMissingNameBean.getMsOrgCorpStructure()%>" id="orgCorpStruc" disabled class="input readonly"   readonly="readonly"  >
			     		<option value="For Profit" <% if("For Profit".equalsIgnoreCase(loMissingNameBean.getMsOrgCorpStructure())) {%>selected<%} %>>For Profit</option>
			        	<option value="Non Profit" <% if("Non Profit".equalsIgnoreCase(loMissingNameBean.getMsOrgCorpStructure())) {%>selected<%} %>>Non Profit</option>
			      	</select></span>
			 </div>
			 
			
			 <!-- Sole Proprietor option for Release 3.10.0 . Enhancement 6572 --> 
			<div style="display: none" class="row" id="entityTypeDiv" name="entityTypeDiv"> 
				<span  class="label"><span class='required'>*</span>Entity Type:</span>
			    <span  class="formfield" >
			    	<select name="entityType" value="<%=loMissingNameBean.getMsOrgEntityType()%>" id="entityType" disabled class="input readonly"   readonly="readonly"  >
			        	<option value="Corporation (any type)" <% if("Corporation (any type)".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>Corporation (any type)</option>
			 			<option value="LLC"<% if("LLC".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>LLC</option>
			 			<option value="Joint Venture" <% if("Joint Venture".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>Joint Venture</option>
			 			<option value="Partnership (any type)" <% if("Partnership (any type)".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>Partnership (any type)</option>
			 			<option value="Sole Proprietor" <% if("Sole Proprietor".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>Sole Proprietor</option>
			 			<option value="Other" <% if("Other".equalsIgnoreCase(loMissingNameBean.getMsOrgEntityType())) {%>selected<%} %>>Other</option>
			      	</select></span>
			 </div>
			 
			 
			<div style="display: none" class="row" id="othersDiv">
				<span class="label"><span class='required'>*</span>Other, Please Specify:</span>
			    <span class="formfield"><input name="others" value="<%=loMissingNameBean.getMsOrgEntityTypeOther()%>" maxlength="150" id="others" class="input readonly"   readonly="readonly"  type="text" /></span>
			</div>		
			 
			<div class="row" id="orgDunNoDiv">
				<span class="label">Dun and Bradstreet Number (DUNS#):</span>
			    <span class="formfield"><input name="orgDunNo" value="<%=loMissingNameBean.getMsOrgDunsNumber()%>" maxlength="9" id="orgDunNo" class="input readonly"   readonly="readonly"  type="text" /></span>
			</div>
			  
			<div class="row" id="orgAltName">
				<span class="label">Doing Business As (DBA) or Alternate Name:</span>
			    <span class="formfield"><input name="orgAltName" value="<%=loMissingNameBean.getMsOrgDoingBusAs()%>" maxlength="100" id="orgAltName" class="input readonly"   readonly="readonly"  type="text" /></span>
			</div>
			  
			<div class="row" id="acctPeriodDiv">
				<span class="label"><span class='required'>*</span>Accounting Period:</span>
			    <span class="formfield">
			    	<select name="acctPeriodFrom" id="acctPeriodFrom" disabled class="input readonly"   style="width:70px" readonly="readonly"  name="">
			     		<option title=" " value=" ">Select</option>
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
			      </select> &nbsp; to &nbsp;
			      <select name="acctPeriodTo" id="acctPeriodTo" disabled class="input readonly"   style="width:70px" readonly="readonly"  name="">
			      		<option title=" " value=" ">Select</option>
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
			      </select></span>
			 </div>
			   
			<div class='hr'></div>
			<h3>Executive Office Address</h3>
			
			<div class="row" id="execAddLine1Div">
				<span class="label"><span class='required'>*</span>Address Line 1:</span>
			    <span class="formfield"><input name="execAddLine1" value="<%=loMissingNameBean.getMsExecAddrLine1()%>" id="execAddLine1" class="input readonly"   readonly="readonly"  type="text" /></span>
			</div>
			 
		    <div class="row" id="execAddLine2Div">
				<span class="label">Address Line 2:</span>
				<span class="formfield">
					<input  name="execAddLine2" id="execAddLine2" value="<%=loMissingNameBean.getMsExecAddrLine2()%>" class="input readonly" readonly="readonly"  type="text" />
				</span>
			</div>
			    
			<div class="row" id="execCityDiv">
				<span class="label"><span class='required'>*</span>City:</span>
			    <span class="formfield">
			    	<input  name="execCity" id="execCity" value="<%=loMissingNameBean.getMsExecCity()%>" class="input readonly"   readonly="readonly"  type="text" />
			    </span>
			</div>
			     
			    
			<div class="row" id="execStateDiv">
				<span class="label"><span class='required'>*</span>State:</span>
				<span class="formfield">
					<select name="execState" id="execState" disabled class="input readonly"   readonly="readonly"  name="select2">
						<option>-</option>
						<c:forEach var="memberTitle" items="${memberState}">
						    <option value="${memberTitle.key}" <c:if test="${memberTitle.key==missingNameBean.msExecState}">selected</c:if>>${memberTitle.value}</option>
						    <option value="${adminTitle.key}" <c:if test="${adminTitle.key==missingNameBean.msAdminOfficeTitle}">selected</c:if>>${adminTitle.value}</option>
					    </c:forEach>
				    </select>
				</span>
			</div>
			   
			<div class="row" id="execZipCodeDiv">
				<span class="label"><span class='required'>*</span>Zipcode:</span>
				<span class="formfield">
					<input  name="execZipCode" value="<%=loMissingNameBean.getMsExecZipCode()%>" id="execZipCode" class="input readonly"   readonly="readonly"  type="text" />
				</span>
			</div>
			    
			<div class="row" id="execPhNoDiv">
				<span class="label"><span class='required'>*</span>Phone Number:</span>
			    <span class="formfield">
			    	<input name="execPhNo" value="<%=loMissingNameBean.getMsExecPhoneNo()%>" id="execPhNo" class="input readonly"   readonly="readonly"  type="text" 
			    	onkeydown="javascript:backspacerDOWN(this,event);" onkeyup="javascript:backspacerUP(this,event);"/>
			    </span>
			</div>
			   
			<div class="row" id="execFaxNoDiv">
				<span class="label">Fax Number:</span>
				<span class="formfield">
					<input name="execFaxNo" value="<%=loMissingNameBean.getMsExecFaxNo()%>" id="execFaxNo" class="input readonly"   readonly="readonly"  type="text" 
					onkeydown="javascript:backspacerDOWN(this,event);" onkeyup="javascript:backspacerUP(this,event);"/>
				</span>
			</div>
			   
			<div class="row" id="execWebSiteDiv">
				<span class="label">Website:</span>
			    <span class="formfield">
			    	<input name="execWebSite" value="<%=loMissingNameBean.getMsExecWebSite()%>" maxlength="60" id="execWebSite" class="input readonly"   readonly="readonly"  type="text" />
			    </span>
			</div>
			<div class='hr'></div>
			 
			<h3>Account Administrator Details</h3>
			
			<div class="row" id="nycIdDiv">
				<span class="label">NYC ID:</span>
			    <span class="formfield">
			    	<input name="nycId" id="nycId" value="<%=loMissingNameBean.getMsAdminNYCId()%>" maxlength="128" type="text" class="input readonly"   readonly="readonly"  value="johnsmith@provider.org" readonly="readonly" />
			    </span>
			 </div>
			 
			<div class="row" id="adminFirstNameDiv">
				<span class="label">First Name:</span>
			    <span class="formfield">
			    	<input name="adminFirstName" value="<%=loMissingNameBean.getMsAdminFirstName()%>" id="adminFirstName" type="text" class="input readonly"   readonly="readonly"  value="John" readonly="readonly" />
			    </span>
			 </div>
					 
			<div class="row" id="adminMiddleNameDiv">
				<span class="label">Middle Name:</span>
			    <span class="formfield"><input name="adminMiddleName" value="<%=loMissingNameBean.getMsAdminMiddleInitial()%>" id="adminMiddleName" class="input readonly"   readonly="readonly"  type="text" value="R" readonly="readonly" /></span>
			</div>
			
			<div class="row" id="adminLastNameDiv">
				<span class="label">Last Name:</span>
				<span class="formfield"><input name="adminLastName" value="<%=loMissingNameBean.getMsAdminLastName()%>" id="adminLastName" class="input readonly"   readonly="readonly"  type="text" value="Smith" readonly="readonly" /></span>
			</div>
			
			<div class="row" id="adminOfficeTitleDiv">
				<span class="label"><span class='required'>*</span>Office Title:</span>
			    <span class="formfield">
			    	<select name="adminOfficeTitle" id="adminOfficeTitle" disabled class="input readonly"   readonly="readonly"  >
			       		<option>-------</option>
			       		<c:forEach var="adminTitle" items="${adminOffTitle}">
				   		<option value="${adminTitle.key}" <c:if test="${adminTitle.key==missingNameBean.msAdminOfficeTitle}">selected</c:if>>${adminTitle.value}</option>
				   		</c:forEach>
				   	</select>
			     </span>
			 </div>
			 
			<div class="row" id="adminPhNoDiv">
				<span class="label"><span class='required'>*</span>Phone Number:</span>
			    <span class="formfield"><input name="adminPhNo" value="<%=loMissingNameBean.getMsAdminPhoneNo()%>" id="adminPhNo" class="input readonly"   readonly="readonly"  type="text" 
			    onkeydown="javascript:backspacerDOWN(this,event);" onkeyup="javascript:backspacerUP(this,event);"/></span>
			</div>
			
			<div class="row" id="adminEmailDiv">
				<span class="label">Email Address:</span>
			    <span class="formfield"><input name="adminEmail" value="<%=loMissingNameBean.getMsAdminEmailAdd()%>" id="adminEmail" class="input readonly"   readonly="readonly"  type="text" value="johnsmith@provider.org" readonly="readonly" /></span>
			</div>
			
			<div class='hr'></div>
			
			<h3>Chief Executive Officer / Executive Director (or equivalent)</h3>
			
			<div class="row" id="ceoFirstNameDiv">
				<span class="label"><span class='required'>*</span>First Name:</span>
			    <span class="formfield">
			    	<input name="ceoFirstName" value="<%=loMissingNameBean.getMsCeoFirstName()%>" maxlength="32" id="ceoFirstName" type="text" class="input readonly"   readonly="readonly"  value="" />
			    </span>
			 </div>
			 
			 <div class="row" id="ceoMiddleNameDiv">
			 	<span class="label">Middle Name:</span>
			    <span class="formfield"><input name="ceoMiddleName" value="<%=loMissingNameBean.getMsCeoMiddleInitial()%>" maxlength="1" id="ceoMiddleName" class="input readonly"   readonly="readonly"  type="text" style='width:36px;' /></span>
			 </div>
			 
			<div class="row" id="ceoLastNameDiv">
				<span class="label"><span class='required'>*</span>Last Name:</span>
			    <span class="formfield"><input name="ceoLastName" value="<%=loMissingNameBean.getMsCeoLastName()%>" maxlength="64" id="ceoLastName" class="input readonly"   readonly="readonly"  type="text" /></span>
			</div>
			
			<div class="row" id="ceoPhNoDiv">
				<span class="label"><span class='required'>*</span>Phone Number:</span>
			    <span class="formfield"><input name="ceoPhNo" value="<%=loMissingNameBean.getMsCeoPhoneNo()%>" id="ceoPhNo" class="input readonly"   readonly="readonly"  type="text" 
			    onkeydown="javascript:backspacerDOWN(this,event);" onkeyup="javascript:backspacerUP(this,event);"/>
			    </span>
			</div>
			
			<div class="row" id="ceoEmailDiv">
				<span class="label"><span class='required'>*</span>Email Address:</span>
			    <span class="formfield"><input name="ceoEmail" value="<%=loMissingNameBean.getMsCeoEmailAdd()%>" id="ceoEmail" class="input readonly"   readonly="readonly"  type="text" /></span>
			</div>
			
			<div class='hr'></div>
			
			<h3>Chief Financial Officer (or equivalent)</h3>
			
			<div class="row" id="isCFODiv">
				<span class="label"></span><span>My organization has a CFO:</span>
				<!-- <input type="radio" name="isCFO"  id="isCFO" value="Yes" checked disabled="disabled" on/>  -->
				<input id="cfoYes" name="isCfo" type="radio" class="radio-btn" disabled="disabled" value="Yes" /> Yes
			 	<input id="cfoNo" checked  name="isCfo" type="radio" class="radio-btn" value="No" disabled="disabled" /> No 
				</span>
			</div>
			
			<div class="row" id="cfoFirstNameDiv" >
				<span class="label"><span class='required'>*</span>First Name:</span>
			    <span class="formfield">
			    	<input name="cfoFirstName" value="<%=loMissingNameBean.getMsCfoFirstName()%>" maxlength="32" id="cfoFirstName" type="text" class="input readonly"   readonly="readonly"  value="" />
			    </span>
			 </div>
			 
			<div class="row" id="cfoMiddleNameDiv" >
				<span class="label">Middle Name:</span>
			    <span class="formfield"><input name="cfoMiddleName" value="<%=loMissingNameBean.getMsCfoMiddleInitial()%>" maxlength="1" id="cfoMiddleName" class="input readonly"   readonly="readonly"  type="text" style='width:36px;' /></span>
			</div>
			
			<div class="row" id="cfoLastNameDiv" >
				<span class="label"><span class='required'>*</span>Last Name:</span>
			    <span class="formfield"><input name="cfoLastName" value="<%=loMissingNameBean.getMsCfoLastName()%>" maxlength="64" id="cfoLastName" class="input readonly"   readonly="readonly"  type="text" /></span>
			</div>
			
			<div class="row" id="cfoPhNoDiv" >
				<span class="label"><span class='required'>*</span>Phone Number:</span>
			    <span class="formfield"><input name="cfoPhNo" value="<%=loMissingNameBean.getMsCfoPhoneNo()%>" id="cfoPhNo" class="input readonly"   readonly="readonly"  type="text" 
			    onkeydown="javascript:backspacerDOWN(this,event);" onkeyup="javascript:backspacerUP(this,event);"/></span>
			</div>
			
			<div class="row" id="cfoEmailDiv" >
				<span class="label"><span class='required'>*</span>Email Address:</span>
			    <span class="formfield"><input name="cfoEmail" value="<%=loMissingNameBean.getMsCfoEmailAdd()%>" id="cfoEmail" class="input readonly"   readonly="readonly"  type="text" /></span>
			</div>
			
			<div class='hr'></div>
			
			<h3>Board Chair / President</h3>
			
			<div class="row" id="presFirstNameDiv">
				<span class="label"><span class='required'>*</span>First Name:</span>
			    <span class="formfield">
			    	<input name="presFirstName" maxlength="32" value="<%=loMissingNameBean.getMsPresFirstName()%>" id="presFirstName" type="text" class="input readonly"   readonly="readonly"  value="" />
			    </span>
			 </div>
			 
			<div class="row" id="presMiddleNameDiv">
				<span class="label">Middle Name:</span>
			    <span class="formfield"><input name="presMiddleName" value="<%=loMissingNameBean.getMsPresMiddleInitial()%>" maxlength="1" id="presMiddleName" class="input readonly"   readonly="readonly"  type="text" style='width:36px;' /></span>
			</div>
			
			<div class="row" id="presLastNameDiv">
				<span class="label"><span class='required'>*</span>Last Name:</span>
			    <span class="formfield"><input name="presLastName" value="<%=loMissingNameBean.getMsPresLastName()%>" maxlength="64" id="presLastName" class="input readonly"   readonly="readonly"  type="text" /></span>
			</div>
			
			<div class="row" id="presPhNoDiv">
				<span class="label"><span class='required'>*</span>Phone Number:</span>
			    <span class="formfield"><input name="presPhNo" value="<%=loMissingNameBean.getMsPresPhoneNo()%>" id="presPhNo" class="input readonly"   readonly="readonly"  type="text" 
			    onkeydown="javascript:backspacerDOWN(this,event);" onkeyup="javascript:backspacerUP(this,event);"/></span>
			</div>
			
			<div class="row" id="presEmailDiv">
				<span class="label"><span class='required'>*</span>Email Address:</span>
			    <span class="formfield"><input name="presEmail" value="<%=loMissingNameBean.getMsPresEmailAdd()%>"" id="presEmail" class="input readonly"   readonly="readonly"  type="text" /></span>
			</div>
			
			<!-- <div class="buttonholder">
			<input name="sbmtAcctReq" id="sbmtAcctReq" type="submit" class="button" title="Submit Account Request" value="Submit Account Request" />
			</div>  -->
			<!-- Form Data Ends -->
			</div>
		</div>
	</div>
	
	<!-- Body Container Ends -->

</form>

<script type="text/javascript">

	//jquery ready function- executes the script after page loading
	$(document).ready(function() { 
		if(	document.getElementById("cfoFirstName").value!=null && 	document.getElementById("cfoFirstName").value!=''){
			enableCFOFields();
		}else{
			disableCFOFields();
		}
		var orgCorpStruc = document.getElementById("orgCorpStruc").value;
		disableEntityType(orgCorpStruc);
		$("#errorUL").removeClass("errorMessages");
		$("#errorUL").html("");
	 
	}); 
	
		// this function is to enable entity type if organization selected is "For-Profit"
	function disableEntityType(orgCorpStruc) {
	
		if( trim(orgCorpStruc) == "For Profit" ) {
			document.getElementById("entityTypeDiv").style.display='block';
			document.getElementById("entityType").style.display='block';
			disableOthers("For Profit");
		}else{
			document.getElementById("entityTypeDiv").style.display='none';
			document.getElementById("entityType").style.display='none';
			disableOthers("Non Profit");
		}
	}
	//this function is for enabling other entity type field if entity type is selected as 'others'
	function disableOthers(entityType) {
	   	var selIndex = document.createadminacct.entityType.selectedIndex;
	   	var comboValue = document.createadminacct.entityType.options[selIndex].value;
	   	if( comboValue == "Other" ) {
		   document.getElementById("othersDiv").style.display='block';
		   document.getElementById("others").style.display='block';
		}else{
		   document.getElementById("othersDiv").style.display='none';
		   document.getElementById("others").style.display='none';
		}
	}
	
	//this function is to enable CFO fields if "Yes" radio button is selected
	function enableCFOFields(){
		document.getElementById("cfoYes").checked= true;
		document.getElementById("cfoFirstNameDiv").style.display='block';
		document.getElementById("cfoFirstName").style.display='block';
		document.getElementById("cfoMiddleNameDiv").style.display='block';
		document.getElementById("cfoMiddleName").style.display='block';
		document.getElementById("cfoLastNameDiv").style.display='block';
		document.getElementById("cfoLastName").style.display='block';
		document.getElementById("cfoPhNoDiv").style.display='block';
		document.getElementById("cfoPhNo").style.display='block';
		document.getElementById("cfoEmailDiv").style.display='block';
		document.getElementById("cfoEmail").style.display='block';
	
	}
	//this function is to disable CFO fields if "No" radio button is selected
	function disableCFOFields(){
		document.getElementById("cfoNo").checked= true;
	    document.getElementById("cfoFirstNameDiv").style.display='none';
		document.getElementById("cfoFirstName").style.display='none';
		document.getElementById("cfoMiddleNameDiv").style.display='none';
		document.getElementById("cfoMiddleName").style.display='none';
		document.getElementById("cfoLastNameDiv").style.display='none';
		document.getElementById("cfoLastName").style.display='none';
		document.getElementById("cfoPhNoDiv").style.display='none';
		document.getElementById("cfoPhNo").style.display='none';
		document.getElementById("cfoEmailDiv").style.display='none';
		document.getElementById("cfoEmail").style.display='none';
		
		document.getElementById("cfoFirstName").value='';
		document.getElementById("cfoMiddleName").value='';
		document.getElementById("cfoLastName").value='';
		document.getElementById("cfoPhNo").value='';
		document.getElementById("cfoEmail").value='';
	}
	function trim(stringToTrim) {
		return stringToTrim.replace(/^\s+|\s+$/g,"");
	}
		
</script>