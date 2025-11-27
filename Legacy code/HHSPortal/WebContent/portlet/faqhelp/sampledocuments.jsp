<%-- This jsp used to display the sample documents for agency --%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.DocumentNameExtension,com.nyc.hhs.constants.ApplicationConstants"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<portlet:defineObjects/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/sampledocuments.js"></script>
<%if(ApplicationConstants.PROVIDER_ORG.trim().equalsIgnoreCase(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE).toString().trim())){ 
%>
<!-- Start QC 9587 R 8.10.0 Remove Contact Us link
<div class="floatRht"> 
	<div class="iconContact" > <a href="javascript:void(0);"  class="terms" onclick="contactUsClick();">Contact Us</a> </div>
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
<h2>Sample Documents</h2>
<div class="hr"></div>
<%-- form content starts from here --%>
<form name="sampleform" action="<portlet:actionURL/>" method ="post" id="sampleform">
<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<input type="hidden" value="" id="nextAction" name="action"/>
	<input type="hidden" value="" id="nextPageParam" name="nextPageParam"/>
	<input type="hidden" value="provider_org" id="orgType" />
	<%-- Below div will be used to filter documents --%>
	<div class="taskButtons floatNone">
      <input type="button" value="${filterLabel}" id= "filterbutton" class="filterDocument" onclick="setVisibility('documentValuePop', 'inline');"/>
      <%-- Below div will appear as one popup when user click on filter button --%>
      <div id="documentValuePop" class='formcontainerFinance' style='width:460px;'>
            <div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
            <div class='row'>
            <%-- below div will populate the document category drop down --%>
                  <span class='label'>Document Category:</span>
                  <span class='formfield'>
                        <select id = "documentCategoryFilter" name="documentCategoryFilter" class="terms" onchange="selectCategory(this.form)">
								<c:forEach var="category" items="${document.categoryList}" >
									<c:set var="selected" value=""></c:set>
									<c:if test="${category==document.docCategory}">
										<c:set var="selected" value="selected"></c:set>
									</c:if>
									<option value="<c:out value="${category}"/>" ${selected}>
									<c:out value="${category}" /></option>
								</c:forEach>
							</select>
                  </span>
                  <span class="error"></span>
            </div>
            <%-- below div will populate the document type drop down --%>
            <div class='row'>
                  <span class='label'>Document Type:</span>
                  <span class='formfield'>
                   <select id = "documentTypeFilter" name="documentTypeFilter" class="terms" disabled="disabled">
								<c:forEach var="type" items="${document.typeList}">
							<c:set var="selected" value=""></c:set>
							<c:if test="${type==document.docType}">
								<c:set var="selected" value="selected"></c:set>
							</c:if>
							<option value="<c:out value="${type}"/>" ${selected}">
							<c:out value="${type}" /></option>
						</c:forEach>
							</select>
                  </span>
                  <span class="error"></span>
            </div>
            <%-- Below div will contain  the filter and reset filter button --%>
            <div class="buttonholder">
                  <input type="button" id="clearfilter" value="Clear Filters" onclick="reset()" class="graybtutton"/>
                  <input type="button" value="Filter" id="filter"/>
            </div> 
      </div>
	<%-- Below div will contain  details of the all documents available --%>
      <div class="tabularWrapper">
      <div id="reRenderId">
      <%-- Below table will be populated by the grid tag handler --%>
            <st:table objectName="sampleList" cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
                  <st:property headingName="Document Type" columnName="sampleType"
                  align="center" size="40%"  />
                  <st:property headingName="Document Category" columnName="sampleCategory"
                  align="right" size="35%" />
                  <st:property headingName="Sample" columnName="actions" align="right" size="25%">
                        <st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentNameExtension" />
                  </st:property>
            </st:table>
           </div>
	</div>
	</div>
</form>