<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<%--Release 5 Proposal Activity and Char 500 History --%>
<!-- Body Wrapper Start -->
<script type="text/javascript">
$(document).ready(function(){
	$('.info-icon').parent().css({
		 'position':'relative',
		'top':'3px'
		});
});
</script>
<form id="myform" action="<portlet:actionURL/>" method="post">
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_6, request.getSession())) {
		if(request.getAttribute("message") == null){
	%>
	<div class="tabularWrapper portlet1Col">
		<div class="tabularCustomHead">Application/Filings</div>
		<table cellspacing="0" cellpadding="0" class="grid">
<!--[Start] R9.1.0 QC9606 Update the Home page for providers  -->
			<tr>	
			    <td>
			        HHS Prequalification is now hosted in PASSPort - it is streamlined and easier than ever to complete!  If your organization wishes to begin the HHS Prequalification application process or has a pending prequalification application in HHS Accelerator, you must submit a new application in PASSPort. To complete an HHS Prequalification Application in PASSPort, you must have a PASSPort account. Click <a class="link" title="PASSPort Login" target="_blank" href="https://passport.cityofnewyork.us/page.aspx/en/usr/login?blockSSORedirect=false&%20ReturnUrl=/page.aspx/en/buy/homepage">here</a> to create a PASSPort account using your NYC.ID or to log into an existing PASSPort account. The <Strong>same</Strong> NYC.ID login credentials you use to access HHS Accelerator may be used to login or create an account in PASSPort.  
                    <br/><br/>
                    Once logged into your PASSPort account,  follow the instructions below to begin the HHS Prequalification Application process in PASSPort: 
                    <br/><br/>
                    1. Navigate to the RFx tab on the top banner and select <Strong>Browse Prequalified Lists</Strong> from the dropdown.  
                     <br/>
                    2. Search for the HHS Accelerator Prequalification application and click on the <Strong>PQL label</Strong> to access the application. 
                     <br/>
                    3. Click the <Strong>Create New Application</Strong> button located at the top of the page, complete the questionnaire, and click <Strong>Submit for Review</Strong>.  
                    <br/><br/>
<!--                     <div class="buttonholder">
                    	<input type="button" class="button" value="Get Started" onclick="window.open( 'https://www1.nyc.gov/site/mocs/systems/about-go-to-passport.page' )"  title = "Get Started" />
                    </div>  -->
			    </td>
		    </tr>
<%--
			<tr>
					<td><span class="">Filings Status:</span>
					<span class="portletTextBold">${filingDetailsBeanKey.FILING_STATUS} </span></td>
		    </tr>
			 <c:if test="${(filingDetailsBeanKey.CORPORATE_STRUCTURE ne 'For Profit') and (filingDetailsBeanKey.FILING_STATUS ne 'Exempt') }">
				<tr class="alternate">
				<td><span class="">Fiscal Period of Last Approved Filing:</span>
					<span class="">${filingDetailsBeanKey.LAST_APPROVED_PERIOD} </span></td>
				</tr>
				<tr>	
					<td><span class="">Last CHAR500 approved on:</span>
					<span class="">${filingDetailsBeanKey.LAST_APPROVED_DATE}</span></td>
				</tr>
				<tr class="alternate">	
					<td><span class="">Next CHAR500 due date:</span>
					<span class="">${filingDetailsBeanKey.DUE_DATE_TO_DISPLAY}<c:if test="${!(empty filingDetailsBeanKey.FY)}">(FY${filingDetailsBeanKey.FY})</c:if> </span></td>
				</tr>
				<tr>	
					<td><span class="">Registration Type:</span>
					<span class="">${filingDetailsBeanKey.REGISTRATION_TYPE}</span></td>
				</tr>
				<tr class="alternate">	
					<td><span class="">Last CHAR500 uploaded on:</span>
					<span class="">${filingDetailsBeanKey.LAST_UPLOADED_DATE}</span></td>
				</tr>

				
				<c:if test="${!(empty filingDetailsBeanKey.TEXT_MESSAGE)}">
					<tr>	
						 <td>
						 	<span class="dateadjust">
								 <c:if test="${filingDetailsBeanKey.CLASS_NAME ne null}">
								 	<span class="${filingDetailsBeanKey.CLASS_NAME}">&nbsp;</span>
								 </c:if>
								 ${filingDetailsBeanKey.TEXT_MESSAGE}
						 	</span>
						 </td>
	            	</tr>
				</c:if>
				<c:if test="${!(empty filingDetailsBeanKey.TEXT_MESSAGE2)}">
				Start R5 updated: defect 8139
					<c:set var="classToUse" value=""/>
					<c:if test="${!(empty filingDetailsBeanKey.TEXT_MESSAGE)}">
						<c:set var="classToUse" value="alternate"/>	
					</c:if>
					<tr class="${classToUse}">	
						 <td>
						 	<span class="dateadjust">
								 <c:if test="${filingDetailsBeanKey.CLASS_NAME2 ne null}">
								 	<span class="${filingDetailsBeanKey.CLASS_NAME2}">&nbsp;</span>
								 </c:if>
						 		${filingDetailsBeanKey.TEXT_MESSAGE2}
						 	</span>
						 </td>
	            	</tr>
				End R5 updated: defect 8139
				</c:if>
			</c:if>
			 --%>
<!--[End] R9.1.0 QC9606 Update the Home page for providers  -->				 
		</table>
	     
	</div>
	<%}else{%>
		<div class="messagediv" id="messagediv" style="width:50%"></div>
		<script type="text/javascript">
			  $(".messagediv").html('<%= request.getAttribute("message")%>');
			  $(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
			  $(".messagediv").show();
		</script>	
	<%}
		}else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</form>
