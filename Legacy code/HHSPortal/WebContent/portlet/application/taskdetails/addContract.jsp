<!-- This page is displayed when a user click on  the add contract button on service question screen.
It will display list of existing contract/we can create a new contract.-->
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.model.ContractDetails" %>
<%@ page import="com.nyc.hhs.model.NYCAgency" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant, org.apache.commons.lang.StringEscapeUtils"%>

<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>

<script type="text/javascript">
function iFrameCancel(value) {
	$("#iFrameCLick").show();
	$("#linkDiv").html("");
	
	window.parent.document.getElementsByTagName("iFrame")[0].contentWindow.scrollTo(0,0);
}
</script>

<style>
	.commentHidden{
		display:none;
	}
	.errorMessages{
		display:none;
	}
	.formcontainer .row span.label{
		width:36%;
	}
	.formcontainer .row span.formfield{
		width:29%;
	}	
	.formcontainer .row span.error{
		float: left;
	   	padding: 4px 0;
	    text-align: left; 
		color:red;
		width:31%;
	}
</style>
<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S070_S104_S070R_PAGE, request.getSession())){%>
	<!-- Body Wrapper Start -->
	<form id="myformContract" name="myformContract" action="<portlet:actionURL/>" method ="post" >
		<c:set var="readOnlyValue" value=""></c:set>
		<c:set var="readOnlyText" value=""></c:set>
	<c:if test="true">
	<input type="hidden" id="contractHidden" value="true"></>
		<c:set var="readOnlyValue" value="disabled=true"></c:set>
		<c:set var="readOnlyText" value="readonly=readonly"></c:set>
	</c:if>
	<input type="hidden" value="" id="checkContractIdHidden" name="checkContractIdHidden"/>
	
	<input type="hidden" value="<%=request.getAttribute("service_app_id")%>" id="hiddenServiceAppId" name="hiddenServiceAppId"/>
	<input type="hidden" value="<%=request.getAttribute("business_app_id")%>" id="hiddenBusinessAppId" name="hiddenBusinessAppId"/>
	<input type="hidden" value="<%=request.getAttribute("section")%>" id="hiddenSectionId" name="hiddenSectionId"/>
	<input type="hidden" value="<%=request.getAttribute("subsection")%>" id="hiddenSubSectionId" name="hiddenSubSectionId"/>
	<input type="hidden" value="${elementId}" id="hiddenElementId" name="hiddenElementId"/>
	
	<input type="hidden" name="buttonHit" id="buttonHit" value="">
	
	
	
	<c:choose>
		<c:when test="${contractIdExist eq null} ">
			<c:set var="contractIdExist" value="false"></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="contractIdExist" value="${contractIdExist}"></c:set>
		</c:otherwise>
	</c:choose>
	
	
	<ul id="errorUL" class='errorMessages'>
    </ul>
	</input>
	<!-- Body Container Starts -->
	<div class="">
	    <h2>Service: ${serviceName}</h2>
	    <!-- Container Starts -->
	    <div class="container"> 
	        
	        <!-- Form Data Starts -->
			<div id="mymain">
					<div class="formcontainer">
	                <h3>Add Contract/Grant Information</h3>
	                <p>Please add information about a contract or grant that best represents your organization’s relevant Service experience in this area. 
	                    Note:  For each contract and/or grant entered, you will be required to upload a scope of work/contract/award letter. You will not be required to upload supporting documentation for any contracts with the City of New York. </p>
	                <p>Please fill out the form below as accurately as possible.
	                <br /><span class="required">*</span>Indicates required fields
	                </p>
	                <br />
	               	<% ContractDetails contractDetails = (ContractDetails)request.getAttribute("reqContract"); 
             			if(contractDetails==null){
              				contractDetails= new ContractDetails();
              			}
               		%>
               			<input type="hidden" name="oldFunderName" value="<%=contractDetails.getMsContractFunderName()%>">
						<input type="hidden" name="oldNYCAgency" value="<%=contractDetails.getMsContractNYCAgency()%>">
						<input type="hidden" name="oldContractId" value="<%=contractDetails.getMsContractID()%>">
						<input type="hidden" name="oldProgramName" value="<%=contractDetails.getMsContractDescription()%>">
	                <h3>Select Existing Contract/Grant</h3>
					<div class="row"> <span class="label">Select from previously added Contracts/Grants</span>
					<span class="formfield">
	                    <select class="input"  name="existingContract" onchange="setSelectionValue(this,
	                    '<%=request.getAttribute("service_app_id")%>',
	                	'<%=request.getAttribute("business_app_id")%>','<%=request.getAttribute("section")%>',
	                	'<%=request.getAttribute("subsection")%>','${elementId}')" id="selectBox"  ${readOnlyValue}>
	                    
	                  	<option  value="-1"></option>
	                  	
						<%
		                    if(request.getAttribute("getValue") != null){				
			  					List<ContractDetails> loContractIdList =(List<ContractDetails>)request.getAttribute("getValue");
			  					Iterator locontractIterator = loContractIdList.iterator();
			  					
			  					while(locontractIterator.hasNext()){
									ContractDetails loContract = (ContractDetails)locontractIterator.next();
									if(request.getAttribute("contractGrant")!= null){
									
									
										 
										
									if(loContract.getMsContractDetailsId().equalsIgnoreCase((String)request.getAttribute("contractGrant"))){
										if(loContract.getMsContractType().equalsIgnoreCase("NYC Government")){
											%>
											<option selected="selected" value="<%=loContract.getMsContractDetailsId()%>" id="<%=loContract.getMsContractID()%>">
											<%=loContract.getMsContractNYCAgency()%> - <%=loContract.getMsContractID()%> - <%=loContract.getMsContractDescription()%></option>
										<%
										} else {
											%>
											<option selected="selected" value="<%=loContract.getMsContractDetailsId()%>" id="<%=loContract.getMsContractID()%>">
											<%=loContract.getMsContractFunderName()%> - <%=loContract.getMsContractID()%> - <%=loContract.getMsContractDescription()%></option>
										<%
										}
									}else{
										if(loContract.getMsContractType().equalsIgnoreCase("NYC Government")){
											%>
											<option value="<%=loContract.getMsContractDetailsId()%>"> <%=loContract.getMsContractNYCAgency()%> - <%=loContract.getMsContractID()%> - <%=loContract.getMsContractDescription()%></option>
										<%
										} else {
											%>
											<option value="<%=loContract.getMsContractDetailsId()%>"> <%=loContract.getMsContractFunderName()%> - <%=loContract.getMsContractID()%> - <%=loContract.getMsContractDescription()%></option>
										<%
										}
										
									%>
									
										
									<%
									
									}
									
									
									
									
									
									} else { 
										
									if(loContract.getMsContractID().equalsIgnoreCase(contractDetails.getMsContractID()) && loContract.getMsContractFunderName().equalsIgnoreCase(contractDetails.getMsContractFunderName()) && loContract.getMsContractDescription().equalsIgnoreCase(contractDetails.getMsContractDescription())){
										if(loContract.getMsContractType().equalsIgnoreCase("NYC Government")){
											%>
											<option selected="selected" value="<%=loContract.getMsContractDetailsId()%>" id="<%=loContract.getMsContractID()%>">
											<%=loContract.getMsContractNYCAgency()%> - <%=loContract.getMsContractID()%> - <%=loContract.getMsContractDescription()%></option>
										<%
										} else {
											%>
											<option selected="selected" value="<%=loContract.getMsContractDetailsId()%>" id="<%=loContract.getMsContractID()%>">
											<%=loContract.getMsContractFunderName()%> - <%=loContract.getMsContractID()%> - <%=loContract.getMsContractDescription()%></option>
										<%
										}
									}else{
										if(loContract.getMsContractType().equalsIgnoreCase("NYC Government")){
											%>
											<option value="<%=loContract.getMsContractDetailsId()%>"> <%=loContract.getMsContractNYCAgency()%> - <%=loContract.getMsContractID()%> - <%=loContract.getMsContractDescription()%></option>
										<%
										} else {
											%>
											<option value="<%=loContract.getMsContractDetailsId()%>"> <%=loContract.getMsContractFunderName()%> - <%=loContract.getMsContractID()%> - <%=loContract.getMsContractDescription()%></option>
										<%
										}
										
									%>
									
										
									<%
									}
									}
								}
		 				 }
					 %>        
	                </select>
                    </span> <span class="error"></span>
					</div>
	            	</div>
	            
	            <!-- Horizontal Row starts --> 
	            <span class="hr"></span> 
	            <!-- Horizontal Row ends -->
	            
	            <h3>Funder</h3>
	            <div class="formcontainer">
					<div class="row"> <span class="label" id="contractTypeDivId"><span class="required">*</span>Type:</span><span class="formfield">
	                    <select class="input" id="contractType" name="contractType" onchange="showMe(this);"  ${readOnlyValue}>
	                        <option value="-1" id="-1"></option>
	                        
	                        <c:forEach var="contractType" items="${contractType}">
	                    		<c:choose>
	                    			<c:when test="${reqContract.msContractType eq contractType.value}">
	                    				<option selected="selected" value="${contractType.value}">${contractType.value}</option>
	                    			</c:when>
	                    			<c:otherwise>
	                    				<option value="${contractType.value}">${contractType.value}</option>
	                    			</c:otherwise>
	                    		</c:choose>
							</c:forEach>
	                        
	                    </select>
	                    </span>
	                    <span class="error"></span>
					</div>
					<div class="row" id="nycAgency"> <span class="label" ><span class="required">*</span>NYC Agency:</span>
						<span class="formfield">
		                    <select class="input" id="nycAgencyId" name="contractNYCAgency"  ${readOnlyValue}>                    
			                    <option id="-1" value="-1"></option>
								
								<c:forEach var="NYCAgencyValues" items="${NYCAgency}">
	                    		<c:choose>
	                    			<c:when test="${reqContract.msContractNYCAgency eq NYCAgencyValues.value}">
	                    				<option selected="selected" value="${NYCAgencyValues.value}">${NYCAgencyValues.value}</option>
	                    			</c:when>
	                    			<c:otherwise>
	                    				<option value="${NYCAgencyValues.value}">${NYCAgencyValues.value}</option>
	                    			</c:otherwise>
	                    		</c:choose>
							</c:forEach>  
								
							</select>
	                    </span>
	                    <span class="error"></span>
	                 </div>
	                 <div class="row" id="funderName" >
		                 <span class="label"><span class="required">*</span>Funder Name:</span> <span class="formfield">
	                     <input class="input" type="text" id="funderNameId" name="contractFunderName" maxlength="60" value="<%=contractDetails.getMsContractFunderName()%>"  ${readOnlyText}/>
	                     </span>
	                     <span class="error"></span>
	                </div>
	                
	                <h3 id="reference" >Reference</h3>
	                <div class="row" id="firstName" title="Funder reference may be any individual within the funder organization who can verify your receipt of funding."> <span class="label"><span class="required">*</span>First Name:</span> <span class="formfield">
	                    <input class="input" type="text" id="firstNameId" name="contractRefFirstName"  maxlength="32" value="<%=contractDetails.getMsContractRefFirstName() %>"  ${readOnlyText}/>
	                    </span><span class="error"></span> </div>
	                <div class="row" id="midName"> <span class="label">Middle Initial:</span> <span class="formfield">
	                    <input class="input" type="text" id="midNameId" name="contractRefMidName" style="width:25px;" maxlength="1" value="<%=contractDetails.getMsContractRefMidName() %>"  ${readOnlyText}/>
	                    </span> <span class="error"></span></div>
	                <div class="row" id="lastName"> <span class="label"><span class="required">*</span>Last Name:</span> <span class="formfield">
	                    <input class="input" type="text" id="lastNameId" name="contractRefLastName" maxlength="64" value="<%=contractDetails.getMsContractRefLastName() %>"  ${readOnlyText}/>
	                    </span> <span class="error"></span></div>
	                <div class="row" id="title"> <span class="label"><span class="required">*</span>Title:</span>
		                <span class="formfield">
		               		<select class="input" id="contractRefTitle" name="contractRefTitle" maxlength="64"  ${readOnlyValue}>
		                    	<option value="-1" id="-1"></option>
		                   		<c:forEach var="staffTitle" items="${staffTitle}">
		                    		<c:choose>
		                    			<c:when test="${reqContract.msContractRefTitle eq staffTitle.key}">
		                    				<option selected="selected" value="${staffTitle.key}">${staffTitle.value}</option>
		                    			</c:when>
		                    			<c:otherwise>
		                    				<option value="${staffTitle.key}">${staffTitle.value}</option>
		                    			</c:otherwise>
		                    		</c:choose>
								</c:forEach>
		                    </select>
	                    </span>
	                    <span class="error"></span>
                   	</div>
	                <div class="row" id="phone"> <span class="label"><span class="required">*</span>Phone Number:</span> <span class="formfield">
	                    <input class="input" id="phoneId" type="text" name="contractRefPhone" value="<%=contractDetails.getMsContractRefPhone() %>"  ${readOnlyText}/>
	                    </span> <span class="error"></span>
                    </div>
	                <div class="row" id="email"> <span id="emailDiv" class="label"><span class="required">*</span>Email Address:</span> <span class="formfield">
	                    <input class="input" type="text" id="email" name="contractRefEmail" maxlength="128" value="<%=contractDetails.getMsContractRefEmail() %>"  ${readOnlyText}/>
	                    </span><span class="error"></span>
                    </div>
	                
	                <!-- Horizontal Row starts --> 
	                <span class="hr"></span> 
	                <!-- Horizontal Row ends -->
	                
					<h3>Contract/Grant Information</h3>
	                <div class="row"> 
	                	<span class="label"><span class="required">*</span>Contract/Grant ID Number:</span> 
	                	<span class="formfield">
		                    <c:set value="<%=contractDetails.getMsContractID()%>" var="checkValue"/>
		                    <c:choose>
		                    	<c:when test="${not empty checkValue || checkValue ne ''}">
		                    		<input class="input" type="text"  id="msContractId" name="msContractId" maxlength="30" value="<%=contractDetails.getMsContractID() %>"  ${readOnlyText}/>
		                    	</c:when>
		                    	<c:otherwise>
		                    		<input class="input" type="text" id="msContractId" name="msContractId" maxlength="30" value="<%=contractDetails.getMsContractID() %>"  ${readOnlyText}/>
		                    	</c:otherwise>
		                    </c:choose>
		                </span>
	                    <span id="msContractIdDiv"> 
		                    <c:if test="${contractIdExist}">
		                    	<span class="individualError">! Contract Id already exist.Please enter other id </span>
		                    </c:if>
	                    </span>
	                    <span class="error"></span>
					</div>

                    <div class="row" id="contractDescriptionDiv" title="Add the name and brief description (no more than 250 characters) of the program you received contract or grant to deliver."> <span style ="height: 100px;" class="label"><span class="required">*</span>Program Name/Description:<p>Briefly describe how your program demonstrates experience in the selected service.</p></span> 
		                <span class="formfield">
		                    <textarea onkeyup="setMaxLength(this,250)" onkeypress="setMaxLength(this,250)" class="textarea floatLft input" id="contractDescription" name="contractDescription" style="display:block" value="<%=StringEscapeUtils.escapeHtml((String)contractDetails.getMsContractDescription()) %>" ${readOnlyText}><%=StringEscapeUtils.escapeHtml((String)contractDetails.getMsContractDescription()) %></textarea>
		                </span>
	                    <span class="error"></span>
                    </div>
	                <div class="row" id="contractStartDateDiv"> <span class="label"><span class="required">*</span>Start Date:</span> <span class="formfield">
	                    <input class="input"  type="text" id="contractStartDate" name = "contractStartDate" style='width:78px;' id='' 
	                    value="<fmt:formatDate pattern='MM/dd/yyyy' value='<%=contractDetails.getMsContractStartDate() %>'/>"  ${readOnlyText} validate="calender" maxlength="10"/>
	                    
	                    <img src="../../../framework/skins/hhsa/images/calender.png" title="Start Date"
	                    	 onclick="return false;"/>
	                    </span> <span class="error"></span><div id="startDate" style="color:red"></div>
                    </div>
	                <div class="row" id="contractEndDateDiv"> <span class="label"><span class="required">*</span>End Date:</span> <span class="formfield">
	                    <input class="input"  type="text" id="contractEndDate" name = "contractEndDate" style='width:78px;' id=''
	                    value="<fmt:formatDate pattern='MM/dd/yyyy' value='<%=contractDetails.getMsContractEndDate() %>'/>"  ${readOnlyText} validate="calender" maxlength="10"/> 
	                    
	                    <img src="../../../framework/skins/hhsa/images/calender.png" title="End Date" onclick="return false;"/>
	                    </span><span class="error"></span>
                    </div>
	                <div class="row" id="contractBudgetDiv"> <span class="label"><span class="required">*</span>Total Contract/Grant Budget:</span> <span class="formfield">
	                    <input class="input" type="text" id="contractBudget" name = "contractBudget" value="<%=contractDetails.getMsContractBudget() %>" maxlength="19"  ${readOnlyText}/>
	                    </span><span class="error"><div id="budgetError" style="color:red"></div></span>
                    </div>
	            </div>
	            <div class='floatRht'>
	            
	           <c:if test="${serviceComments ne null and ! empty serviceComments}">
							<%@include file="showServiceCommentsLink.jsp" %>
							 <div class="commentHidden" style="padding:10px;">
								<c:forEach var="loopItems" items="${serviceComments}" varStatus="counter">
								     	<c:if test="${counter.index ne 0}">
								     	 -------------------------------------------------<br>
								     	</c:if>
							     <b>${loopItems['USER_ID']} - <fmt:formatDate pattern="MM/dd/yyyy" value="${loopItems['AUDIT_DATE']}" /></b><br>
							      ${loopItems['DATA']}	<br>
						     </c:forEach>
					     </div>
					</c:if>
				</div>
				
				<div class='clear'></div>
	            <!-- Horizontal Row starts --> 
	            <span class="hr"></span>
	             
	            <!-- Horizontal Row ends -->
	            <div class="formcontainer"></div>
	            
	            <div class="buttonholder">
	                <input id="cancelform" type="button" class="graybtutton" value="Cancel" title="Cancel" name="cancelform" onclick="iFrameCancel('staff')"/>
	                 
	            </div>
	            <!-- Form Data Ends -->
            <!-- Form Data Ends --> 
			</div>
	        
	        <!-- Container Ends --> 
		</div>
	    
	    <!-- Body Container Ends --> 
	    
	</div>
	<!-- Body Wrapper End -->
	
	
	</form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>

