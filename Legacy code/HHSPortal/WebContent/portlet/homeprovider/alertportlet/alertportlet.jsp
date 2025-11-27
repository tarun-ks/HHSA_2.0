<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<script type="text/javascript">
	$(document).ready(function() {
		if('null' != '<%=request.getAttribute("message")%>'){
			$(".errordiv").html('<%=request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('errordiv', this)\" />");
		    $(".errordiv").addClass('<%= request.getAttribute("messageType")%>');
		    $(".errordiv").show();
		}
	});
	
	function selectAllAndSubmit() {
		document.alertform.next_action.value="showpage";	
		document.alertform.action = document.alertform.action + "&app_menu_name=alert_icon";
		document.alertform.submit();
	}
</script >

<form action="<portlet:actionURL/>" method="post" name="alertform">
		<input type="hidden" name="next_action" value="" />
		<div class="errordiv" id="errordiv"></div>
		<div  class="tabularWrapper portlet1Col">
			<div class="tabularCustomHead">Alerts</div>
			<table cellspacing="0" cellpadding="0"  class="grid">
		    	<tr>
		        	<td>
		        	<span class="portletTextBold" >
		            	<%if(Integer.valueOf(request.getAttribute("alertInboxCount").toString()) != 0){ %>
		                	<a href="#" onclick="selectAllAndSubmit();" title='${alertInboxCount} Alerts.'>${alertInboxCount}</a>
		            	<%}else{ %> ${alertInboxCount}<%} %>  
		            </span> Alerts remaining in your Alerts inbox
		        	</td>   
				</tr>
			</table>
		</div>	
</form>	
