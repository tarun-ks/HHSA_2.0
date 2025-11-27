<!--This jsp will include all other screen for Basic's document, language and geography/Board's documents etc.-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.ArrayList"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@ page import="com.nyc.hhs.model.ApplicationAuditBean"%>
<%@ page import="com.nyc.hhs.util.DateUtil"%>
<%@ page import="java.util.Date"%>

<script type="text/javascript" src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/calendar.js"></script>
<jsp:include page="sectionHeader.jsp"/>
<portlet:defineObjects />
<style>
	.commentHidden{
		display:none;
	}
	.commentsBorder {
	border-bottom: 2px solid #EFEFEF;
    padding: 10px 0;
    width: 95%;
	}
	
</style>
<c:if test="${printView ne null}">
<style>
					.formcontainer table{
						clear: both;
					    width:100%;
					}
					.formcontainer table .header{
						color: #5077AA;
					    font-size: 13px;
					    font-weight: bold;
					    line-height: 18px;
					    padding: 0 0 10px;
					}
					.formcontainer table .questions{
						margin: 0 8px 3px 0;
					    min-height: 20px;
					    padding: 4px 5px 5px 0;
					    text-align: right;
					    width: 49.5%;
					    border-bottom:3px solid #fff;
					    background: #f2f2f2;
					}
					.formcontainer table .answers{
						min-height: 20px;
					    padding: 4px 0 4px 6px;
					    text-align: left;
					    width: 48%;
					}
					.tableHeaderPrint{
						 background-color: #E4E4E4;
					}
					.tableRowEvenPrint{
						background-color: #FFFFFF;
					}
					.tableRowOddPrint{
					 	background-color: #F2F2F2;
					}
					.content td{
						width: 300px;
						padding-left: 10px;
					}
					.content table{
						width: 80%;
					}
					.content table.documentTablePrint{
						width: 90%;
					}
					.content th{
						font-weight: bold;
						padding-left: 10px;
					}
					.noContent{
						border:1px solid grey;
						padding: 5px 0 5px 10px;
					}
					table{
						padding-bottom:20px;
					}
					.headingFont{
						font-wight:bold;
						font-size: 18px;
					}
					.sectionHeading{
						font-size: 16px;
					}
					td br{
						display: block;
					}
					.print-heading{
						background: none;
						font-size:17px;
						font-weight:bold;
						text-decoration:underline;
					}
					.headingText{
						border-bottom: 2px solid black;
					    margin-bottom: 3px;
					    padding-top: 7px;
					}
					.subheading{
						font-weight: bold;
					}
					h2{
						width: 85%;
					}
					.taskButtons {
						clear: both;
					}
					</style>
					</c:if>
<script type="text/javascript">
//action  url.
	var actionUrl  = '<portlet:renderURL/>'+"&bussAppStatus="+'${bussAppStatus }'+"&business_app_id="+'${business_app_id }'+"&section="+'${section }'+"&subsection="+'${subsection }'+"&forUpdate="+'${forUpdate}' ;
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
	ArrayList<ApplicationAuditBean> loTaskHistoryList =null;	
	if(portletSessionScope.get("aoTaskHistoryList")!=null){
		loTaskHistoryList = (ArrayList<ApplicationAuditBean>)portletSessionScope.get("aoTaskHistoryList");
		if(!loTaskHistoryList.isEmpty()){ %>
			<c:if test="${(org_type eq 'provider_org' and section ne 'businessapplicationsummary')or (org_type eq 'city_org' and app_menu_name ne 'inbox_icon' and section ne 'businessapplicationsummary')}">
				<%@include file="showCommentsLink.jsp" %>
			</c:if>
			<div class="commentHidden">
			<ul>
			
			<%
			String lsDate=null;
				for(int i=0;i<loTaskHistoryList.size();i++)
				{
					lsDate=DateUtil.getDateMMddYYYYFormat(loTaskHistoryList.get(i).getMsAuditDate());%>	
					<li class="commentsBorder">		
					<b>Accelerator - <%=lsDate%>:</b><br>                         
					<%=(loTaskHistoryList.get(i).getMsData())%>
					</li>
				<% 
				}
				%>
				
				</ul>
				</div>
		<%}}
	%> 
 	<br/>
 	<jsp:scriptlet>
		if(null == request.getParameter("removeNavigator")){
	</jsp:scriptlet>
	<script>
	//method to ser section status
		<c:forEach var="map1" items="${loBusinessStatusBeanMap}">
			setStatusSection("${map1.key}", "${map1.value.msSectionStatus}");
		</c:forEach>
	</script>
	<c:if test="${section != 'businessapplicationsummary' && section != 'servicessummary'}">
 		<div class="customtabs">
 			<ul>
				<li><a class="sub-${loBusinessStatusBeanMap[section].moHMSubSectionDetails['questions']}" id="subsection_questions" title="Questions" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="questions" /><portlet:param name="next_action" value="showquestion" /><portlet:param name="bussAppStatus" value="${bussAppStatus }" /></portlet:renderURL>">Questions</a></li>
					<c:if test="${section != 'policies'}">
						<li><a class="sub-${loBusinessStatusBeanMap[section].moHMSubSectionDetails['documentlist']}" id="subsection_documentlist" title="Documents" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="documentlist"/><portlet:param name="next_action" value="open" /><portlet:param name="bussAppStatus" value="${bussAppStatus }" /></portlet:renderURL>">Documents</a></li>
					</c:if>
				<c:if test="${section == 'basics'}">
					<li><a class="sub-${loBusinessStatusBeanMap[section].moHMSubSectionDetails['geography']}" id="subsection_geography" title="Geography" href='<portlet:renderURL><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="geography"/><portlet:param name="next_action" value="open" /><portlet:param name="bussAppStatus" value="${bussAppStatus }" /></portlet:renderURL>'>Geography</a></li>
					<li><a class="sub-${loBusinessStatusBeanMap[section].moHMSubSectionDetails['languages']}" id="subsection_languages" title="Languages" href='<portlet:renderURL><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="languages"/><portlet:param name="next_action" value="open" /><portlet:param name="bussAppStatus" value="${bussAppStatus }" /></portlet:renderURL>'>Languages</a></li>
					<li><a class="sub-${loBusinessStatusBeanMap[section].moHMSubSectionDetails['populations']}" id="subsection_populations" title="Populations" href="<portlet:renderURL><portlet:param name="section" value="${section }" /><portlet:param name="subsection" value="populations"/><portlet:param name="next_action" value="open" /><portlet:param name="bussAppStatus" value="${bussAppStatus }" /></portlet:renderURL>" >Populations</a></li>
				</c:if>
			</ul>
	   </div>
	</c:if>
	<c:if test="${section == 'businessapplicationsummary'}">
 		<div class="customtabs clear">
 			<div class="floatRht capitalize">
				<b>Application Status: </b>${bussAppStatus}
			</div>
 			<ul>
				<li><a id="subsection_applicationsummary" title="Business Application Summary" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" />
					<portlet:param name='section' value='businessapplicationsummary'/>
					<portlet:param name='subsection' value='applicationsummary'/><portlet:param name="next_action" value="showquestion" /><portlet:param name="bussAppStatus" value="${bussAppStatus }" />
					</portlet:renderURL>">Business Application Summary</a>
				</li>
				
				<%-- Start : R5 Added --%>
		 <% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S062_PAGE, request.getSession())){ %>
				<li><a id="subsection_applicationhistory" title="Application History & Comments" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" />
					<portlet:param name='section' value='businessapplicationsummary'/>
					<portlet:param name='subsection' value='applicationhistory'/><portlet:param name="next_action" value="open" /><portlet:param name="bussAppStatus" value="${bussAppStatus }" />
					</portlet:renderURL>">Application History & Comments</a>
				</li>
				<%} %>
				<%-- End : R5 Added --%>
			</ul>
	   </div>
	</c:if>
	<c:if test="${section == 'servicessummary' && (subsection != 'addservice' && subsection != 'summary')}">
 		<div class="customtabs">
 			<ul>
				<li><a id="subsection_servicesummary" href="#" title="Service Summary">Service Summary</a></li>
				<li><a id="subsection_servicehistory" title="Service History & Comments" href="<portlet:renderURL><portlet:param name="business_app_id" value="${business_app_id }" />
					<portlet:param name='section' value='serviceapplicationsummary'/>
					<portlet:param name='subsection' value='servicehistory'/><portlet:param name="next_action" value="open" /><portlet:param name="bussAppStatus" value="${bussAppStatus }" />
					</portlet:renderURL>">Service History & Comments</a>
				</li>
			</ul>
	   </div>
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
		<c:choose>
	      <c:when test="${printView eq null}">
	     	 <jsp:include page="<%=lsFilePath%>"></jsp:include>
	      </c:when>
	      <c:otherwise>
	      	<div class="formcontainer">
				${printView}
			</div>
	      </c:otherwise>
		</c:choose>
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
	<div style="clear: both;">&nbsp;</div>