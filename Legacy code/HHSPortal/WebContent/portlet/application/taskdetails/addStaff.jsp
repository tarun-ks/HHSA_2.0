<!-- This page is displayed when a user click on  the add staff button on service question screen.
It will display list of existing contract/we can add a new staff.-->
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.*"%>
<%@ page import="com.nyc.hhs.model.StaffDetails" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" 
                                                  prefix="fn" %>
<script type="text/javascript" src="../../../resources/js/addStaff.js"></script>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<portlet:defineObjects/>
<style>
	.commentHidden{
		display:none;
	}
	.errorMessages{
		display:none;
	}
	.formcontainer{
		position: static;
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

<script type="text/javascript" src="${pageContext.servletContext.contextPath}/WebContent/framework/skeletons/hhsa/js/util.js"></script>

<script type="text/javascript">
function iFrameCancel(value) {
	$("#iFrameCLick").show();
	$("#linkDiv").html("");
	window.parent.document.getElementsByTagName("iFrame")[0].contentWindow.scrollTo(0,0);
}
</script>	
<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.SA_S071_S105_S071R_PAGE, request.getSession())){%>
	<!-- Body Wrapper Start -->
	<form id="myformStaff" name="myformStaff" action="<portlet:actionURL/>" method ="post" >
		<c:set var="readOnlyValue" value=""></c:set>
		<c:set var="readOnlyText" value=""></c:set>
	<c:if test="${true}">
		<c:set var="readOnlyValue" value="disabled=disabled"></c:set>
		<c:set var="readOnlyText" value="readonly=readonly"></c:set>
	</c:if>
	<input type="hidden" name="buttonHit" id="buttonHit" value="">
	
	<input type="hidden" value="<%=request.getAttribute("service_app_id")%>" id="hiddenServiceAppId" name="hiddenServiceAppId"/>
	<input type="hidden" value="<%=request.getAttribute("business_app_id")%>" id="hiddenBusinessAppId" name="hiddenBusinessAppId"/>
	<input type="hidden" value="<%=request.getAttribute("section")%>" id="hiddenSectionId" name="hiddenSectionId"/>
	<input type="hidden" value="<%=request.getAttribute("subsection")%>" id="hiddenSubSectionId" name="hiddenSubSectionId"/>
	<input type="hidden" value="${elementId}" id="hiddenElementId" name="hiddenElementId"/>
	<ul id="errorUL" class='errorMessages'>
    </ul>
	</input>
	<input type="hidden" id="selectStaffValue" name="selectStaffValue" value="<%=request.getAttribute("selectedStaff")%>"></input>
	<!-- Body Container Starts -->
			
	    <h2>Service: ${serviceName}</h2>
	    <!-- Container Starts -->
	    <div class="container"> 
	        
        	<!-- Form Data Starts -->
        	<div id="mymain">
	            <div class="formcontainer">
	                <h3>Add Staff Members</h3>
	                
	                <p>Please add information below about a key staff person that represents your organization’s relevant service experience in this area. You will be required to upload the resume for each staff member added to complete your Service Application. </p>
	                <p>
	                <br /><span class="required">*</span>Indicates required fields
	                </p>
	                <br />
	                <h3>Select Existing Staff Member</h3>
	                
	                
	                <% StaffDetails staffDetails=new StaffDetails();
	                if(request.getAttribute("reqStaff")!=null){
	                	staffDetails = (StaffDetails)request.getAttribute("reqStaff"); 
              			if(staffDetails==null){
	               			staffDetails= new StaffDetails();
              			}}
	               	%>
					<div class="row"> <span class="label">Select Staff Member</span>
						<span class="formfield">
		                    <select class="input" name="existingStaff" onchange="setSelectionValue(this,
		                    '<%=request.getAttribute("service_app_id")%>',
		                	'<%=request.getAttribute("business_app_id")%>','<%=request.getAttribute("section")%>',
		                	'<%=request.getAttribute("subsection")%>','${elementId}')" id="selectBox" ${readOnlyValue}>
		                  	<option value="-1" id="-1"></option>
	                  	
	                  		<%
		                    List<StaffDetails> loStaffIdList = new ArrayList<StaffDetails>();
		                    if(request.getAttribute("getValue") != null){				
			  					loStaffIdList =(List<StaffDetails>)request.getAttribute("getValue");
			  					Iterator locontractIterator = loStaffIdList.iterator();
			  					while(locontractIterator.hasNext()){
			  					
									StaffDetails loStaff = (StaffDetails)locontractIterator.next();
									%>
			  						<%=loStaff.getMsStaffId() %><%=loStaff.getMsStaffFirstName() %>
			  						<%
									if(loStaff.getMsStaffId().equalsIgnoreCase(staffDetails.getMsStaffId())){
									%>
										<option selected="selected" value="<%=loStaff.getMsStaffId()%>"   id="<%=loStaff.getMsStaffId()%>"><%=loStaff.getMsStaffFirstName() + " " +loStaff.getMsStaffMidInitial() + " " +loStaff.getMsStaffLastName()%></option>
									<%
									}else{
									%>
										<option value="<%=loStaff.getMsStaffId()%>"  id="<%=loStaff.getMsStaffId()%>"><%=loStaff.getMsStaffFirstName() + " " +loStaff.getMsStaffMidInitial() + " " +loStaff.getMsStaffLastName()%></option>
									<%
									}
								}
		 				 	}
					 		%>
							</select>
                    	</span>
                    	<span class="error"></span>
					</div>
				</div>
	            
	            <!-- Horizontal Row starts --> 
	            <span class="hr"></span> 
	            <!-- Horizontal Row ends -->
	            
	            <h3>New Staff Member</h3>
	            <div class="formcontainer">
	                
                <div class="row" id="firstName"> <span class="label"><span class="required">*</span>First Name:</span> <span class="formfield">
                    <input class="input" type="text" id="firstNameId" name="staffFirstName" maxlength="32" value="<%=staffDetails.getMsStaffFirstName() %>" ${readOnlyText}/>
                    </span><span class="error"></span>
                </div>
	                
	               
                <div class="row" id="midName"> <span class="label">Middle Initial</span> <span class="formfield">
                    <input class="input" type="text" id="midNameId" name="staffMidInitial" maxlength="1"  style="width:25px;" maxlength="1" value="<%=staffDetails.getMsStaffMidInitial() %>" ${readOnlyText}/>
                    </span><span class="error"></span>
                </div>
	                
                <div class="row" id="lastName"> <span class="label"><span class="required">*</span>Last Name:</span> <span class="formfield">
                    <input class="input" type="text" id="lastNameId" name="staffLastName" maxlength="64" value="<%=staffDetails.getMsStaffLastName() %>" ${readOnlyText}/>
                    </span> <span class="error"></span></div>
                <div class="row" id="title"> <span class="label"><span class="required">*</span>Title:</span>
	                <span class="formfield">
		                <select class="input" id="staffTitle" name="staffTitle" ${readOnlyValue}>
		                 <option value="-1" id="-1"></option>
		                	<c:forEach var="staffTitle" items="${staffTitle}">
		                 		<c:choose>
		                 			<c:when test="${reqStaff.msStaffTitle eq staffTitle.key}">
		                 				<option selected="selected" value="${staffTitle.key}">${staffTitle.value}</option>
		                 			</c:when>
		                 			<c:otherwise>
		                 				<option value="${staffTitle.key}">${staffTitle.value}</option>
		                 			</c:otherwise>
		                 		</c:choose>
							</c:forEach>
		                 </select>
		             </span><span class="error"></span>
                 </div>
                <div class="row" id="phone"> <span class="label"><span class="required">*</span>Phone Number:</span>
                	<span class="formfield">
                    	<input class="input" type="text" id="staffPhone" name="staffPhone" value="<%=staffDetails.getMsStaffPhone() %>" ${readOnlyText}/>
                    </span> <span class="error"></span>
                </div>
                <div class="row" id="email"> <span class="label"><span class="required">*</span>Email Address:</span>
                	<span class="formfield">
                    	<input class="input" type="text" id="emailId" name="staffEmail" maxlength="128" value="<%=staffDetails.getMsStaffEmail() %>" ${readOnlyText}/>
                    </span> <span class="error"></span>
                </div>	               
	         
				
	            <!-- Horizontal Row starts --> 
	            <span class="hr"></span> 
	            <!-- Horizontal Row ends -->
	            <div class="formcontainer"></div>
	            <!-- Form Data Ends -->
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
	            <div class="buttonholder">
	                <input id="cancelform" type="button" class="graybtutton" value="Cancel" title="Cancel" name="cancelform" onclick="iFrameCancel('staff')"/>
	            </div>
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

