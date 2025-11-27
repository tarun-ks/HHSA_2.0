<div>
	<div id="finalStatisticPortlet">
		<%@page import="java.util.ArrayList, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
		<%@page language="java" contentType="text/html;charset=UTF-8"%>
		<%@page import="com.nyc.hhs.frameworks.grid.*"%>
		<%@ page errorPage="/error/errorpage.jsp" %>
		<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
		<portlet:defineObjects/>
		<script type="text/javascript">
		var loadingBars = new Array();
		// This function is used to display procurement counts
		function finalStatisticRefresh()
		{
			$("#finalStatisticPortlet").loadingHome("Loading...");
			hhsAjaxRender(null, document.getElementById("systemStatistic"), "finalStatisticPortlet", document.getElementById("systemStatistic").action, "loadingCallBackFromInitialStatisticRefresh");
		}
		// This function is used to remove waiting icon
		function loadingCallBackFromInitialStatisticRefresh()
		{
			$("#finalStatisticPortlet").loadingHomeClose();
		}
		</script>
		<portlet:resourceURL var='statisticPortlet' id='statisticPortlet' escapeXml='false'>
		</portlet:resourceURL>
		<!-- Body Wrapper Start -->
		<form id="systemStatistic" name="systemStatistic" action="${statisticPortlet}" method ="post" >
			<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S028_PAGE, request.getSession())) {%>
				<div  class="tabularWrapper portlet1Col homepageHHS">
		  			<div class="tabularCustomHead">System Statistics
		  				<a href="javascript:;" onclick="javascript:finalStatisticRefresh()"><img src="../framework/skins/hhsa/images/refresh.png" class="tabularCustomHeadIcon" title="Refresh" alt="Refresh"/></a>
		  			</div>
					<table cellspacing="0" cellpadding="0"  class="grid">                 
						<tr>
							<c:if test="${visibiltyFlag}">
			                	<td style="color: #1569B2;" class = "portletTextBold">
			                		<c:choose>
			                			<c:when test="${approvedProviderStatus ne null}">${approvedProviderStatus}</c:when>
			                			<c:otherwise>0</c:otherwise>
			                		</c:choose>
			                	</td>
		                	</c:if>
		                    <td>Total Providers with an 'Approved' Provider Status</td>
						</tr>
		                <tr class="alternate">
		                	<c:if test="${visibiltyFlag}">
			                	<td style="color: #1569B2;" class = "portletTextBold">
			                		<c:choose>
			                			<c:when test="${draftStatus ne null}">${draftStatus}</c:when>
			                			<c:otherwise>0</c:otherwise>
			                		</c:choose>
			                 	</td>
			                 </c:if>
		                 	<td>Total Providers with 'Draft'  Business Applications</td>
						</tr>
		                <tr>
		                	<c:if test="${visibiltyFlag}">
			                	<td style="color: #1569B2;" class = "portletTextBold">
			                		<c:choose>
			                			<c:when test="${inReviewStatus ne null}">${inReviewStatus}</c:when>
			                			<c:otherwise>0</c:otherwise>
			                		</c:choose>
			                	</td>
			                </c:if>
		                    <td>Total Providers with 'In Review'  Business Applications</td>
						</tr>
		                <tr class="alternate">
		                	<c:if test="${visibiltyFlag}">
			                	<td style="color: #1569B2;" class = "portletTextBold">
			                		<c:choose>
			                			<c:when test="${returnedForRevisionStatus ne null}">${returnedForRevisionStatus}</c:when>
			                			<c:otherwise>0</c:otherwise>
			                		</c:choose>
			                   	</td>
			                 </c:if>
		                    <td>Total Providers with 'Returned for Revisions'  Business Applications</td>
						</tr>
					</table>
				</div>
			<%}else{ %>
		 	  <h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		 	<%} %>
		</form>	
	</div>
</div>
