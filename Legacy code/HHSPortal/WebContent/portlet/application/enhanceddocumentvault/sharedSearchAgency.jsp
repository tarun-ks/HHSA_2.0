<!-- Initial Imports -->
<%@page import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
<%@page import="com.nyc.hhs.model.Document"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil"%>
<%@page import="com.nyc.hhs.constants.ComponentMappingConstant,org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.Map,java.util.HashMap,com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb,java.util.List, java.util.Iterator"%>
<%@page import="com.nyc.hhs.model.DocumentPropertiesBean"%>
<!-- End -->
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<!-- Including Tag Libraries -->
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!-- End -->
<!-- Including Error page -->
<%@ page errorPage="/error/errorpage.jsp" %>
<!-- End -->
<!-- Portlets Url's -->
<portlet:defineObjects/>
<portlet:resourceURL var="openTreeAjax" id="openTreeAjax" escapeXml="false"></portlet:resourceURL>
<portlet:resourceURL var="checkLinkage" id="checkLinkage" escapeXml="false"></portlet:resourceURL>
<portlet:renderURL var="checkLocking" id="checkLocking"></portlet:renderURL>

<!-- End -->
<!-- Including Style Sheets -->
<link rel="stylesheet" href="../css/documentlist.css" type="text/css"></link>
<link rel="stylesheet" href="../css/style.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<!-- End -->
<!-- Including javaScript Files -->
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/jstree.min.js"></script> 
<script type="text/javascript" src="../js/enhancedprovdocumentlist.js"></script>
<script>

$(document).ready(function() {
});
</script>
<!-- End -->
<!-- Jsp Body Starts -->

<form id="myform" action="<portlet:actionURL/>" method ="post" name='provform'>
<!-- Hidden Parameters -->
	<input type="hidden" id="checkLinkageParam" value="${checkLinkage}"/>
	<input type="hidden" id="hdnopenTreeAjaxVar" value="${openTreeAjax}"/>
	<input type="hidden" id="checkedDocumentId" value=""/>
	<input type="hidden" id="checkedDocumentType" value=""/>
	<input type="hidden" id="checkLocking" value="${checkLocking}"/>
	<input type="hidden" name="next_action" value="" id="next_action"/>
	<input type="hidden" id="agencySet" value="${agencySet}"/>
	<input type="hidden" id="providerSet" value="${providerSet}"/>
	<input type="hidden" id="filterStatus" value="${filterStatus}"/>
	<input type="hidden" id="message" value="${message}"/>
	<input type="hidden" name="messageType" id ="messageType" value="${messageType}" />
	<input type="hidden" name="presentFolderId" id ="presentFolderId" value='' />
	<input type="hidden" name="selectedfolderidformove" id ="selectedfolderidformove" value='' />
	<input type="hidden" id="deleteForever" value="${deleteForever}"/>
	<input type="hidden" id="role_current" name = "role_current" value="<%= session.getAttribute("role_current") %>"/>	
<!-- End -->
<h2>Document Vault</h2>
<c:if test="${org_type ne 'city_org'}">
	<div class="iconQuestion"><a href="javascript:void(0);"
		title="Need Help?" onclick="pageSpecificHelp('Document Vault');"></a></div>
</c:if>
<div class='hr'></div>
<div>Manage documents loaded by your organization.</div>
<div class="messagediv" id="messagediv"></div>
<!-- Document Vault Header -->
<div class="vaultheader" id="u43" data-label="MenuHeader">
<img id="u43_img" src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/menuheader.png">
<span style="position: absolute;margin-top: 10px;cursor: pointer;left:240px;" id="newfolder";"><img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/newfolder_icon.png" align="left"><span style="top:8px;color:white;position: relative;">New Folder</span></span>
<span style="position: absolute;margin-top: 10px;cursor: pointer;left:640px;" id="restore";"><img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/newfolder_icon.png" align="left"><span style="top:8px;color:white;position: relative;">Restore</span></span>
<span style="color:white;position: absolute;margin-top:10px;cursor: pointer;left:350px;" id="uploadDoc"><img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/newfolder_icon.png" align="left" /><span style="top:8px;color:white;position: relative;">Upload Document</span></span>
<span style="color:white;opacity:0.5;padding:3px;position: absolute;margin-top:7px;cursor: pointer;right:350px;" id="file_menu"><img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/file_icon.png" align="left" /><span style="top:4px;color:white;position: relative;">File Options</span></span>
<span style="color:white;position: absolute;margin-top:10px;cursor: pointer;right:250px;" id="searchDocument" onclick="searchDoc()"><img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/newfolder_icon.png" align="left" /><span style="top:8px;color:white;position: relative;">Search</span></span>
<span  style="position: absolute;margin-top: 10px;cursor: pointer;left:240px;display:none" id="emptyBin" onclick="emptyRecycleBin()"><img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/emptybin_icon.png" align="left"><span title="Permanently delete all items in your organization’s recycle bin" style="top:4px;color:white;position: relative;" >Empty Bin</span></span>
</div>
<jsp:include page="search.jsp"></jsp:include>
<div class="vaultheader" id="findOrgDocbtn" data-label="MenuHeader" style="display: none;">
<img id="u43_img1" src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/menuheader.png">
<span style="color: white; position: relative; top: -40px; cursor: pointer; left: 850px;" class="findOrgDocbtn">
<img src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/newfolder_icon.png"/>
<span style="position:relative;top:-10px;color:white;">Search</span>
</span>
<span id="removeList" style="color: white; cursor:pointer;display: none; position: relative; top: -50px">X Remove from list</span>
</div>
<!-- End -->
<!-- Chat Box  -->
<div id='chatbox-content1' style='display:none;'>
<img src='../framework/skins/hhsa/images/information_icon.png' alt='someimage'  align='left'/>Move</a>
<hr><a href='#' onclick='shareEntityInfo()'><img src='../framework/skins/hhsa/images/share_icon.png' alt='someimage'  align='left'/>Share</a>
<br><br><a href='#' onclick='myfunc()'><img src='../framework/skins/hhsa/images/unshare_icon.png' alt='someimage'  align='left'/>UnShare</a>
<br><br><hr><a href='#' onclick='myfunc()'><img src='../framework/skins/hhsa/images/delete_icon.png' alt='someimage'  align='left'/>Delete</a>
</div>

<div class='clear'></div>
<!-- End -->

<!-- Form Data Starts -->
<div id="mymain">
<div class="" style="height:500px;">
<div class="leftTree">
	<div id="leftTree"></div>
	<ul>
	<hr width="85%">
	<li class="findOrgDoc" style="cursor:pointer;"><img src="../framework/skins/hhsa/images/search_icon.png" style="width: 20px;height: 20px;margin-left: 10px;margin-right: 5px;" align="left"><b>Find Org Documents</b></li>
	<li class="selectOrg" style="cursor:pointer;"onclick="orgOpen()"><img src="../framework/skins/hhsa/images/select_organization.png" style="width: 20px;height: 20px;margin-left: 10px;margin-right: 5px;" align="left"><b>Select Organization</b></li>
	</ul>
	<br>
	<div id="itemlist">
	<ul></ul>
	</div>
</div>
			<!-- Restructured grid parameters for R5 -->
			<div  id="documentVaultGrid" class="tabularWrapper tabularWrapperDocumentVault">
				<st:table objectName="documentList"  cssClass="heading"
					alternateCss1="evenRows" alternateCss2="oddRows" pageSize='<%=(Integer)session.getAttribute("allowedObjectCount")%>'>
					<%-- <st:property toolTip = "Select all" headingName="<input type='checkbox' onchange='checkAll(this)' name='chkAll'/>" columnName="documentId" align="center" size="5%" >
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.SharedSearchExtension" />
					</st:property> --%>
					<st:property headingName="Document Name" columnName="docName" align="center" sortType="docName" sortValue="asc" size="30%">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.SharedSearchExtension" />
					</st:property>
					<st:property headingName="Organization Name" columnName="OrgName" sortType="OrgName" sortValue="asc" align="center" size="20%">
					<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.SharedSearchExtension" />
					</st:property>
					<st:property headingName="Modified Date" columnName="date"
						align="right" size="25%" sortType="date" sortValue="asc">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.SharedSearchExtension" />
						</st:property>
						<st:property headingName="" columnName="fileOptions" align="center" size="5%">
						<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.SharedSearchExtension" />
					</st:property>
				</st:table>
				<c:choose>
					<c:when test="${fn:length(documentList) eq 0 and searchMessageFlag eq 'true'}">
						<div class="noRecord" id="noDocumentDiv">No documents found. Revise Search criteria</div>
					</c:when>
					<c:when test="${fn:length(documentList) ne 0 }">
						<div class="noRecord" id="noDocumentDiv"></div>
					</c:when>
					<c:otherwise>
					<c:choose>
						<c:when test="${org_type eq 'city_org'}">
						<div class="noRecord" id="noDocumentDiv">Use Search Box above to locate specific documents across all organizations.</div>
						</c:when>
						<c:otherwise>
						<div class="noRecord" id="noDocumentDiv">Use Search Box above to locate specific documents that are shared with your organization.</div>
						</c:otherwise>	
						</c:choose>				
					</c:otherwise>
				</c:choose>
				<input type="hidden" id="hideEmptyRecycleBin" value="${fn:length(documentList)}"/>
				<input type="hidden" id="sharedSearch" value="${sharedFlag}"/>
				
				<!-- Added for Download All -->
				
				<input type="hidden" name="documentName" id="documentName" value="${documentName}"/>
				<input type="hidden" name="documentType" id="documentType" value="${documentType}"/>
				<input type="hidden" name="modifiedFrom" id="modifiedFrom" value="${modifiedFrom}"/>
				<input type="hidden" name="modifiedTo" id="modifiedTo" value="${modifiedTo}"/>
				<input type="hidden" name="DownloadsharedWith" id="DownloadsharedWith" value="${sharedWith}"/>
				<input type="hidden" name="downloadFlag" id="downloadFlag" value="${dbFlag}"/>
				<input type="hidden" id="documentListForDownloadAll" value="${fn:length(documentList)}" /> 
				<input type="hidden" id="role_current" name = "role_current" value="<%= session.getAttribute("role_current") %>"/>
				<!-- End -->
				
			</div>
			</div>
			<!-- Grid Ends -->
			</div>
<!-- End -->
</form>
<div class="overlay"></div>
<!--   Jsp Includes -->
<jsp:include page="documentvaultoverlay.jsp"></jsp:include>
<%--

--%>
<!-- End -->
<c:if test="${param.next_action eq 'openProviderView' or lsAction eq  'openProviderView'}">
<input type="hidden" name="action" value="documentVault" id="searchDocumentVaultId"/>
</c:if>
<input type="hidden" id="isOrganizationSharesDoc" name="isOrganizationSharesDoc" value="${isOrganizationSharesDoc}" />
<div id="overlayedJSPContent" style="display:none"></div>


<!-- End -->