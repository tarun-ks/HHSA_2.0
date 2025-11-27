<%-- This jsp used to display the help documents for agency --%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.DocumentNameExtension,com.nyc.hhs.constants.ApplicationConstants"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<portlet:defineObjects/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/helpdocumentsagency.js"></script>
<%if(ApplicationConstants.PROVIDER_ORG.trim().equalsIgnoreCase(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE).toString().trim())){ 
%>
<!-- Start QC 9587 R 8.10.0 Remove Contact Us link
<div class="floatRht"> 
	<div class="iconContact" > <a href="javascript:void(0);"  class="terms" title="Contact Us" onclick="contactUsClick();">Contact Us</a> </div>
</div>
	-->
	<div>
		<!--[Start] Update Language      R9.6.1 QC9693	-->
		If you need assistance, please visit <a  href="https://www.nyc.gov/mocshelp"  target="_blank" style="color:#5077AA;">www.nyc.gov/mocshelp.</a>
		<!-- [End] Update Language      R9.6.1 QC9693	-->
	</div>
	<!-- 
End QC 9587 R 8.10.0 Remove Contact Us link
-->
<%} %>
<h2>Help Documents</h2>
<div class="hr"></div>
<form name="helpformagency" action="<portlet:actionURL/>" method ="post" >
	<input type="hidden" value="" id="nextAction" name="action"/>
	<input type="hidden" value="" id="nextPageParam" name="nextPageParam"/>
	<input type="hidden" value="agency_org" id="requestingOrgType" name="requestingOrgType"/>
<%-- Below div will be used to filter documents --%>
<div class="taskButtons floatNone">
	<input type="button" value="${filterLabel}" id= "filterbutton" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');"/>
<%-- Below div will appear when user click on filter button --%>
<div id="documentValuePop" class='formcontainerFinance' style='width:460px;'>
		<div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
		<%-- below div will populate the help category drop down --%>
		<div class='row'>
			<span class='label'>Help Category:</span>
			<span class='formfield'>
				  <select id = "documentCategoryFilter" name="documentCategoryFilter" class="terms" onchange="selectCategory(this.form)">
								<c:forEach var="category" items="${document.helpCategoryList}" >
									<c:set var="selected" value=""></c:set>
									<c:if test="${category==document.helpCategory}">
										<c:set var="selected" value="selected"></c:set>
									</c:if>
									<option value="<c:out value="${category}"/>" ${selected}>
									<c:out value="${category}" /></option>
								</c:forEach>
							</select>
			</span>
			<span class="error"></span>
		</div>
		<%-- Below div will contain  the filter and reset filter button --%>
		<div class="buttonholder">
			<input type="button" id="clearfilter"  value="Clear Filters" onclick="reset()" class="graybtutton"/>
			<input type="button" value="Filter" id="filter"/>
		</div> 
	</div>
	<%-- Below div will contain  details of the all documents available --%>
	<div class="tabularWrapper">
		<st:table objectName="helpList"  cssClass="heading"
			alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
			<st:property headingName="Document Title" columnName="docName" align="center" size="30%">
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameExtension" />
		    </st:property>
			<st:property headingName="Help Category" columnName="helpCategory"
				align="right" size="30%" />
			<st:property headingName="Document Description" columnName="documentDescription"
				align="right" size="40%" />
		</st:table>
	</div>
	</div>
</form>