<%@page import="com.nyc.hhs.util.HHSPortalUtil"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.nyc.hhs.constants.*"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<portlet:defineObjects />
<script>
function printSpecificLock(lockId)
{
	var url = $("#printSpecificLockURL").val()+"&printSpecificLock="+lockId;
	window.location.href=url;
	}
	
function cleanSpecificLock(lockId)
{
	var url = $("#cleanSpecificLockURL").val()+"&cleanSpecificLock="+lockId;
	window.location.href=url;
	}
function reCache()
{
	var url = $("#reCacheURL").val();
	window.location.href=url;
	}
function reinitializeLog4j()
{
	var url = $("#reinitializeLog4jURL").val();
	window.location.href=url;
	}
function updateCache()
{
	var url = $("#updateCacheURL").val();
	window.location.href=url;
	}
function cleanCache()
{
	var url = $("#cleanCacheURL").val();
	window.location.href=url;
	}
</script>
<style></style>
<portlet:actionURL var='adminSettingsUrl' escapeXml='false'>
		<portlet:param name="action" value="adminSettings" />
		<portlet:param name="isAdminUser" value="${isAdminUser}" />
</portlet:actionURL>
<portlet:renderURL var='printSpecificLockURL' escapeXml='false'>
		<portlet:param name="action" value="adminSettings" />
		<portlet:param name="isAdminUser" value="${isAdminUser}" />
</portlet:renderURL>
<portlet:renderURL var='cleanSpecificLockURL' escapeXml='false'>
		<portlet:param name="action" value="adminSettings" />
		<portlet:param name="isAdminUser" value="${isAdminUser}" />
</portlet:renderURL>
<portlet:renderURL var='reCacheURL' escapeXml='false'>
		<portlet:param name="action" value="adminSettings" />
		<portlet:param name="reCache" value="true" />
		<portlet:param name="isAdminUser" value="${isAdminUser}" />
</portlet:renderURL>
<portlet:renderURL var='reinitializeLog4jURL' escapeXml='false'>
		<portlet:param name="action" value="adminSettings" />
		<portlet:param name="reinitializeLog4j" value="true" />
		<portlet:param name="isAdminUser" value="${isAdminUser}" />
</portlet:renderURL>
<portlet:renderURL var='cleanCacheURL' escapeXml='false'>
		<portlet:param name="action" value="adminSettings" />
		<portlet:param name="cleanCache" value="true" />
		<portlet:param name="isAdminUser" value="${isAdminUser}" />
</portlet:renderURL>
<portlet:renderURL var='updateCacheURL' escapeXml='false'>
		<portlet:param name="action" value="adminSettings" />
		<portlet:param name="updateCache" value="coherenceElement" />
		<portlet:param name="isAdminUser" value="${isAdminUser}" />
</portlet:renderURL>
<form:form id="adminSettings" name="adminSettings" action="${adminSettingsUrl}" method="post">
<input type="hidden" value="${adminSettingsUrl}" id="adminSettingsUrl"/>
<input type="hidden" value="${printSpecificLockURL}" id="printSpecificLockURL"/>
<input type="hidden" value="${cleanSpecificLockURL}" id="cleanSpecificLockURL"/>
<input type="hidden" value="${reCacheURL}" id="reCacheURL"/>
<input type="hidden" value="${reinitializeLog4jURL}" id="reinitializeLog4jURL"/>
<input type="hidden" value="${cleanCacheURL}" id="cleanCacheURL"/>
<input type="hidden" value="${updateCacheURL}" id="updateCacheURL"/>
<input type="hidden" value="<%=HHSPortalUtil.parseQueryString(request,"isAdminUser")%>" id="isAdminUser" name="isAdminUser"/>
<div class='hr'></div>
<h2>Screen Lock Maintenance</h2>
			<c:if test="${message ne null}">
				<div class="passed" id="messagediv" style="display:block">Operation Executed SuccessFully!!!<img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" onclick="showMe('messagediv', this)">
				</div>
			</c:if>
<c:if test="${printSpecificLock ne true and keysIds ne null and fn:length(keysIds) gt 0}">
<div id="printAllLockIds" style="display:block;">
	<c:choose>
		<c:when test="${keysIds ne null and fn:length(keysIds) gt 0}">
		<span class="bold">Active Locks:</span> 
			<c:forEach var="lockId" items="${keysIds}" >
				<a class="link" href="#" onclick="printSpecificLock('<c:out value="${lockId}"/>')"><c:out value="${lockId}"/></a>
			</c:forEach>	
		</c:when>
		<c:otherwise> 
			<div class="failed" style="display:block">Currently There are no Active Locks</div>
		</c:otherwise>
	</c:choose>
</div>
</c:if>
<c:if test="${printSpecificLock eq true }">
<div id="printSpecificLock" style="display:block;">
	<br />
	<span class="bold">Lock ID:</span> ${lockId}
	<br />
	<span class="bold">Lock Details:</span> ${lockDescribe}	
	<br /><a class="link" href="#" onclick="cleanSpecificLock('${lockId}')">Release Lock</a>
</div>
</c:if>
<br/><br/>
			<%
	   			if(null != HHSPortalUtil.parseQueryString(request,"isAdminUser") && HHSPortalUtil.parseQueryString(request,"isAdminUser").equalsIgnoreCase("hhsAdmin"))
	   			{
	   		%>
	   		<ul>
		        <li>1. <a class="link" title="ReCache" href="#" onclick="reCache()">ReCache</a></li>
		        <br/>
		        <li>2. <a class="link" title="Reinitialize Log4j" href="#" onclick="reinitializeLog4j()">Reinitialize Log4j</a></li>
		        <br/>
		        <li>3. <a class="link" title="Release All Locks" href="#" onclick="cleanCache()">Release All Locks</a></li>
		        <br/>
		        <li>4. <a class="link" title="Update Cache" href="#" onclick="updateCache()">Update Cache</a></li>
		     </ul>
	        <br/><br/>
	        <a class="link bold" title="Administrator Settings - Screen Locking Details" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_admin_settings_portlet&_nfls=false&removeNavigator=true&navigatefrom=landing&printCache=true&isAdminUser=hhsAdmin">Refresh</a>
	   		<%
	   			}else{
	   		%>
	   		<a class="link bold" title="Administrator Settings - Screen Locking Details" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_admin_settings_portlet&_nfls=false&removeNavigator=true&navigatefrom=landing&printCache=true">Refresh</a>
	   		<%} %>
	   		<a class="link bold" href="<%=request.getContextPath()%>/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintenancelanding&_nfls=false&app_menu_name=header_maintenance" title="Access maintenance screens for Taxonomy and FAQ's" >Back</a>
</form:form>