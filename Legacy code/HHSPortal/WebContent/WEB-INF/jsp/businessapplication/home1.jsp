<!--This jsp will include all other screen of services document, specialization and service setting etc.-->
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.ArrayList"%>
<%@ page errorPage="/error/errorpage.jsp" %>

<jsp:include page="sectionHeader.jsp"/>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/calendar.js"></script>


<script type="text/javascript">
//action url.
		var actionUrl  = '<portlet:renderURL/>'+"&section="+'${section }'+"&subsection="+'${subsection }'+"&forUpdate="+'${forUpdate}'+"&elementId="+'${elementId}';
		function submitForm(anchor){
			$('#'+anchor).attr("href", actionUrl) ;
	}
</script>
<%
	String lsMenu="";
	if(renderRequest.getAttribute(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU) != null){
		lsMenu = (String)renderRequest.getAttribute(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU);
	}
	String lsFilePath = "";
	if(renderRequest.getAttribute("fileToInclude") != null){
		lsFilePath = (String) renderRequest.getAttribute("fileToInclude");
	}
	if(renderRequest.getAttribute("filePathForDocumentList") != null){
		lsFilePath = (String) renderRequest.getAttribute("filePathForDocumentList");
	}
	ArrayList loTaskHistoryList =null;	
	if(portletSessionScope.get("aoTaskHistoryList")!=null){
		loTaskHistoryList = (ArrayList)portletSessionScope.get("aoTaskHistoryList");
		if(!loTaskHistoryList.isEmpty()){ %>
		<%@include file="showCommentsLink.jsp" %> 
	<%}}
 	%>
 	<br/>
 	<jsp:scriptlet>
		if(null == request.getParameter("removeNavigator")){
	</jsp:scriptlet>
	<script>
	//method to set section status.
		<c:forEach var="map1" items="${loBusinessStatusBeanMap}">
			setStatusSection("${map1.key}", "${map1.value.msSectionStatus}");
		</c:forEach>
	</script>
	
	<c:if test="${section != 'businessapplicationsummary'}">
	<c:if test="${deactivatedService ne null and  deactivatedService}">
		<div class="failed" style="display:block">You cannot submit your application as it is no longer required to apply for ${serviceName}. Please remove the service from your application.</div>
		<br></br>
	</c:if>
		<c:if test="${subsection ne 'addservice'}">
	 		<div class="customtabs">
	 			<ul>
						<li><a class="sub-${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['questions']}" id="subsection_questions" title="Answer questions about your organization's ability to provide this Service" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="questions" /><portlet:param name="next_action" value="showServiceQuestion" /><portlet:param name="elementId" value="${elementId}" /></portlet:renderURL>">Questions</a></li>
						<li><a class="sub-${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['documentlist']}" id="subsection_documentlist" title="Upload any required documents for this Service Application" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="documentlist"/><portlet:param name="next_action" value="open" /><portlet:param name="elementId" value="${elementId}" /></portlet:renderURL>">Documents</a></li>
						<li><a class="sub-${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['specialization']}" id="subsection_specialization" title="Indicate your organization's specialization for this Service if applicable" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="specialization"/><portlet:param name="next_action" value="open" /><portlet:param name="elementId" value="${elementId}" /></portlet:renderURL>">Specialization</a></li>
						<li><a class="sub-${loServicesStatusBeanMap[service_app_id].moHMSubSectionDetails['servicesetting']}" id="subsection_servicesetting" title="Indicate the setting(s) in which your organization has the ability to provide this Service" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="servicesetting"/><portlet:param name="next_action" value="open" /><portlet:param name="elementId" value="${elementId}" /></portlet:renderURL>">Service Setting</a></li>
				</ul>
		   </div>
		  </c:if>
	</c:if>
   	<jsp:scriptlet>
		}
	</jsp:scriptlet>
   <script type="text/javascript">
   //show selected method.
		showSelected('${section }','${subsection }');
	</script>
    <div id="tabs-container">
		<!-- Form Data Starts -->
		<div id="mymain">
			<c:if test="${param.fromTaskDetails eq null}">
				<div class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Applications');"></a></div>
			</c:if>
			<jsp:include page="<%=lsFilePath%>"></jsp:include>
		</div>
	</div>
	<div class="overlay"></div>
	<div class="alert-box-help">
	       <div class="content">
	             <div id="newTabs" class='wizardTabs'>
	                   <div class="tabularCustomHead">Applications - Help Documents</div>
	             <div id="helpPageDiv"></div>
	             </div>
	       </div>
	       <a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
	</div>
	<div class="alert-box-contact">
		<div class="content">
			<div id="newTabs">
				<div id="contactDiv"></div>
			</div>
		</div>
	</div>