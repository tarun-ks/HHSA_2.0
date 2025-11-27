<!--This will display the header for application when application is submitted.
This jsp will include all other screen like Basic's document, language and geography/Board's documents etc.-->
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.ArrayList"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<jsp:include page="sectionHeader.jsp"/>
<portlet:defineObjects />
<script type="text/javascript">
		var actionUrl  = '<portlet:renderURL/>'+"&section="+'${section }'+"&subsection="+'${subsection }'+"&forUpdate="+'${forUpdate}' ;
		//submits the form
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
	ArrayList loTaskHistoryList =null;	
	if(portletSessionScope.get("aoTaskHistoryList")!=null){
		loTaskHistoryList = (ArrayList)portletSessionScope.get("aoTaskHistoryList");
		if(!loTaskHistoryList.isEmpty()){ %>
	<%	}
		}
	 %>
 <br/>
	<script>
		<c:forEach var="map1" items="${loBusinessStatusBeanMap}">
			setStatusSection("${map1.key}", "${map1.value.msSectionStatus}");
		</c:forEach>
	</script>
	<div class="customtabs" style="border-bottom: none;">
		<ul id="removeEmptyUL">
			<c:if test="${section ne 'policies' and section ne 'businessapplicationsummary'}">
			<li><a class="sub-${loBusinessStatusBeanMap[section].moHMSubSectionDetails['questions']}" id="subsection_questions" title="Questions" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="questions" /><portlet:param name="next_action" value="showquestion" /></portlet:renderURL>">Questions</a></li>
		</c:if>
		<c:if test="${section ne 'policies' and section ne 'businessapplicationsummary'}">
				<li><a class="sub-${loBusinessStatusBeanMap[section].moHMSubSectionDetails['documentlist']}" id="subsection_documentlist" title="Documents" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="documentlist"/><portlet:param name="next_action" value="open" /></portlet:renderURL>">Documents</a></li>
		</c:if>
		<c:if test="${section eq 'basics'}">
			<li><a class="sub-${loBusinessStatusBeanMap[section].moHMSubSectionDetails['geography']}" id="subsection_geography" title="Geography" href='<portlet:renderURL><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="geography"/><portlet:param name="next_action" value="open" /></portlet:renderURL>'>Geography</a></li>
			<li><a class="sub-${loBusinessStatusBeanMap[section].moHMSubSectionDetails['languages']}" id="subsection_languages" title="Languages" href='<portlet:renderURL><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="languages"/><portlet:param name="next_action" value="open" /></portlet:renderURL>'>Languages</a></li>
			<li><a class="sub-${loBusinessStatusBeanMap[section].moHMSubSectionDetails['populations']}" id="subsection_populations" title="Populations" href="<portlet:renderURL><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="populations"/><portlet:param name="next_action" value="open" /></portlet:renderURL>" >Populations</a></li>
		</c:if>
	</ul>
  </div>
	   
   <c:if test="${section == 'businessapplicationsummary'}">
		<div class="customtabs">
		<div class="floatRht capitalize">
			<b>Application Status: </b>${bussAppStatus}
		</div>
			<ul>
			<li><a id="subsection_applicationsummary" title="Business Application Summary" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" />
				<portlet:param name='section' value='businessapplicationsummary'/>
				<portlet:param name='subsection' value='applicationsummary'/><portlet:param name="next_action" value="showquestion" /><portlet:param name="bussAppStatus" value="${bussAppStatus }" />
				</portlet:renderURL>">Business Application Summary</a>
			</li>
			<li><a id="subsection_applicationhistory" title="Application History & Comments" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" />
				<portlet:param name='section' value='businessapplicationsummary'/>
				<portlet:param name='subsection' value='applicationhistory'/><portlet:param name="next_action" value="displayApplicationHistory" /><portlet:param name="bussAppStatus" value="${bussAppStatus }" />
				</portlet:renderURL>">Application History & Comments</a>
			</li>
		</ul>
   </div>
</c:if>
	
   <script type="text/javascript">
		showSelected('${section }','${subsection }');
	</script>
   <div id="tabs-container">
	<!-- Form Data Starts -->
	<div id="mymain">
	<c:if test="${isViewDoc ne 'yes'}">
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