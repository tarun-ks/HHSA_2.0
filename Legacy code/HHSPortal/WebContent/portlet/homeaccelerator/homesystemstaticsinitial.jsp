<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<script type="text/javascript">

	function onsystemStatistictRefresh(){
		document.systemStatistic.submit();
	}
</script>
<!-- Body Wrapper Start -->
<form id="systemStatistic" name="systemStatistic" action="<portlet:actionURL/>" method ="post" >
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S028_PAGE, request.getSession())) {%>
		<div  class="tabularWrapper portlet1Col">
  			<div class="tabularCustomHead">System Statistics
  				<a href="javascript:;" onclick="javascript:onsystemStatistictRefresh()"><img src="../framework/skins/hhsa/images/refresh.png" class="tabularCustomHeadIcon" title="Refresh" alt="Refresh"/></a>
  			</div>
			<table cellspacing="0" cellpadding="0"  class="grid">                 
				<tr>
                    <td>Total Providers with an 'Approved' Provider Status</td>
				</tr>
                <tr class="alternate">
                 	<td>Total Providers with 'Draft'  Business Applications</td>
				</tr>
                <tr>
                    <td>Total Providers with 'In Review'  Business Applications</td>
				</tr>
                <tr class="alternate">
                    <td>Total Providers with 'Returned for Revisions'  Business Applications</td>
				</tr>
			</table>
		</div>
	<%}else{ %>
 	  <h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
 	<%} %>
</form>	
