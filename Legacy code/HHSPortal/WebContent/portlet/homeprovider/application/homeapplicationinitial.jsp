<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<portlet:defineObjects/>

<script>
	function startNewApplicationHome(){
		document.myform.action =$("#contextPath").val()+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&lsTermsConditions=termsAndCondition";
		document.myform.submit();
	}
	function openNewWindow(url){
		newWindow = window.open(url,"_blank","toolbar=yes,menubar=yes,status=0,copyhistory=0,scrollbars=yes,resizable=1,location=0") ;
   		newWindow.location = url;
	}
</script>



<form id="myform" action="<portlet:actionURL/>" method ="post" name="myform">
	<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
	<!--  
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_4, request.getSession())) {%>
	-->
		<div class="tabularWrapper portlet1Col">
			<table cellspacing="0" cellpadding="0" class="grid">
				<tr>
					<td>
						<p>--No organizations have shared documents with you at this time. This section will become active once an organization has granted you access to 1 or more documents.</p>
							<p>If you'd like to grant Providers or NYC Agencies view-only access to your documents, you can do so from your 
							<a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=enhanced_document_vault_page&_nfls=false&app_menu_name=header_document_vault&removeNavigator=true&headerClick=true">Document Vault</a>.
							</p>
					</td>
				</tr>
			</table>
		</div>

<!-- 
		<div  class="tabularWrapper portlet1Col">
     		<div class="tabularCustomHead">Application</div>
	     	<c:choose>
	     		<c:when test="${isDataAvailable}">
	     			<table cellspacing="0" cellpadding="0"  class="grid">                 
	                	<tr>
	                    	<td class='capitalize'>Your current organization status:<span class="portletTextBold">${organizationStatus}</span></td>
	                  	</tr>
	                  	<tr class="alternate">
	                    	<td class='capitalize'>Your Business Application status:<span class="portletTextBold">${businessAppStatus}</span></td>
	                  	</tr>
						<tr>
	                    	<td> Your organization has 
		                    	<c:choose>
		                    		<c:when test="${numberOfServices > 0}">
		                    			<span class="portletTextBold"><a title="${numberOfServices}" alt="${numberOfServices}" href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=${userType}">${numberOfServices}</a></span><span>&nbsp; pending Service Applications</span>
		                    		</c:when>
		                    		<c:otherwise>
		                    			<span class="portletTextBold">${numberOfServices}</span><span>&nbsp; pending Service Applications</span>
		                    		</c:otherwise>
		                    	</c:choose>
	                    	</td>
						</tr>
	                    <tr class="alternate">
	                    	<td>Your organization has 
	                    	<c:choose>
	                    		<c:when test="${numberOfApprovedService > 0}">
	                    			<span class="portletTextBold"><a title="${numberOfApprovedService}" alt="${numberOfApprovedService}"  href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_business_summary&_nfls=false&app_menu_name=header_application&first_action=${userType}">${numberOfApprovedService}</a></span><span>&nbsp; approved Service Applications</span>
	                    		</c:when>
	                    		<c:otherwise>
	                    			<span class="portletTextBold">${numberOfApprovedService}</span><span>&nbsp; approved Service Applications</span>
	                    		</c:otherwise>
	                    	</c:choose>
							</td>    
						</tr>
 -->							
						<!-- Start : R5 Added 
						<c:if test="${bussAppExpiringDate ne null and businessAppStatus ne 'Approved'}">
						<jsp:useBean id="today" class="java.util.Date" />
						<fmt:parseDate value="${bussAppExpiringDate}" pattern="MM/dd/yyyy" var="formatedDate" />
						
	                    	<c:if test="${formatedDate lt today }">
	                    	<tr>
	                    	<td>
	                    		<span class="red-ex-mark"/> Your Business Application expired on ${bussAppExpiringDate}
	                    		</td>    
						</tr>
	                    	</c:if>
	                    	<c:if test="${formatedDate gt today}">
	                    	<tr>
	                    	<td>
	                    		<span class="red-ex-mark"/> Your Business Application is expiring on ${bussAppExpiringDate}
	                    		</td>    
						</tr>
	                    	</c:if>
							
						</c:if>
						<c:if test="${serviceAppExpiringDate ne null}">
						<c:set var="altClass" value=""/>
						<c:if test="${bussAppExpiringDate ne null}">
							<c:set var="altClass" value="alternate"/>
						</c:if>
						<tr class="${altClass}">
	                    	<td>
	                    		<span class="red-ex-mark"/> 1 or more Service Applications are expiring on ${serviceAppExpiringDate}
							</td>    
						</tr>
						</c:if>
						<!-- End : R5 Added  
	                </table>
				</c:when>
	     		<c:otherwise>
	     			<table cellspacing="0" cellpadding="0"  class="grid">                  
	                	<tr>
	                    	<td>
			                    <p>Your organization has not started an HHS Accelerator Application.</p>
			                    <p>If you would like to start the application process, click the "Get Started" button below. </p>
			                    <p>To read about the potential opportunities in applying to provide services to the citizens of New York City, 
			                    	<a href="#" title="click here" onclick="openNewWindow('http://www.nyc.gov/hhsaccelerator')">click here</a>.
			                    </p>
			                    <div class="buttonholder">
			                    	<input type="submit" class="button" value="Get Started" title = "Get Started" />
			                    </div>
	                    	</td>                                       
	                	</tr>
	            	</table>
	     		</c:otherwise>
			</c:choose>
		</div>
		-->
	<!--   
	<%}else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
	-->
</form>	
