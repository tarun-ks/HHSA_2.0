<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<!-- Body Wrapper Start -->
<form id="myform" action="<portlet:actionURL/>" method ="post" >
	<!--   
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S027_PAGE_4, request.getSession())) {%>
	-->
	<div  class="tabularWrapper portlet1Col">
		<div class="tabularCustomHead">Application
	  		<img src="../framework/skins/hhsa/images/refresh.png" class="tabularCustomHeadIcon" title="Refresh" alt="Refresh"/>
	  	</div>

		<table cellspacing="0" cellpadding="0"  class="grid">                 
	    	<tr>
	        	<td>Your current organization status:</td>
	            <td><span class="portletTextBold"><a href="#">In Review</a></span></td>
			</tr>
	        
	        <tr class="alternate">
	        	<td>Your Business Application status:</td>
	            <td><span class="portletTextBold"><a href="#">Returned for Revisions</a></span></td>
			</tr>
	        
	        <tr>
	        	<td>Your organization has applied for</td>
	            <td><span class="portletTextBold"><a href="#">5</a></span><span>&nbsp; services</span></td>
			</tr>
	        
	        <tr class="alternate">
	        	<td>Your organization has been approved for</td>
	            <td><span class="portletTextBold"><a href="#">0</a></span><span>&nbsp; services</span></td>
			</tr>
		</table>
	</div>
	<!--  
	<%}else{ %>
 		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
 	-->
</form>	
