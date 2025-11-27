<%-- This jsp used to display pop APT Progress when E-pin is assigned --%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Overlay Popup Starts --%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/award.js"></script>
    <div class="tabularContainer"> 
	<h2>View APT Progress</h2>
	<div class='hr'></div>
	<div class="formcontainer">
		<div class="row">
			  <span class="label"><label for=''>Provider Name:</label></span>
			  <c:choose>
			  	<c:when test="${AwardBean eq null}">
			  		<span class="formfield" >--</span>
			  	</c:when>
			  	<c:otherwise>
			  		<span class="formfield">${AwardBean.providerName}</span>
			  	</c:otherwise>
			  </c:choose>
			  
		</div>
		<div class="row">
			  <span class="label"><label for=''>Award E-PIN:</label></span>
			  <c:choose>
			  	<c:when test="${AwardBean eq null}">
			  		<span class="formfield" >--</span>
			  	</c:when>
			  	<c:otherwise>
			  		 <span class="formfield">${AwardBean.awardEpinId}</span>
			  	</c:otherwise>
			  </c:choose>
		</div>
		<div class="row">
			  <span class="label"><label for=''>Contract ID:</label></span>
			  <c:choose>
			  	<c:when test="${AwardBean eq null}">
			  		<span class="formfield" >--</span>
			  	</c:when>
			  	<c:otherwise>
			  		<span class="formfield">${AwardBean.ctNumber}</span>
			  	</c:otherwise>
			  </c:choose>
			  
		</div>
		<div class="row">
			  <span class="label"><label for=''>Award Amount ($):</label></span>
			  <c:choose>
			  	<c:when test="${AwardBean eq null}">
			  		<span class="formfield" >--</span>
			  	</c:when>
			  	<c:otherwise>
			  		<span class="formfield" id="awardAmt1">${AwardBean.awardAmount}</span>
			  	</c:otherwise>
			  </c:choose>
			  
		</div>
		<div class="row">
			  <span class="label"><label for=''>Status:</label></span>
			  <c:choose>
			  	<c:when test="${AwardBean eq null}">
			  		<span class="formfield" >--</span>
			  	</c:when>
			  	<c:otherwise>
			  		<span class="formfield">
						  ${AwardBean.status}
			  		</span>
			  	</c:otherwise>
			  </c:choose>
		</div>
	</div>

<h3>APT Information</h3>	
<div class="formcontainer">	
	<div class="row">
			  <span class="label"><label for=''>Award Agency ID:</label></span>
			  <c:choose>
			  	<c:when test="${AwardBean eq null}">
			  		<span class="formfield" >--</span>
			  	</c:when>
			  	<c:otherwise>
			  		<span class="formfield">${AwardBean.awardAgencyId}</span> 
			  	</c:otherwise>
			  </c:choose>
			  
		</div>
		<div class="row">
			  <span class="label"><label for=''>Current APT Milestone(s):</label></span>
			  <c:choose>
			  	<c:when test="${AwardBean eq null}">
			  		<span class="formfield" >--</span>
			  	</c:when>
			  	<c:otherwise>
			  		<span class="formfield">${AwardBean.aptMilestone}</span>
			  	</c:otherwise>
			  </c:choose>
			  
		</div>
		<div class="row">
			  <span class="label"><label for=''>MOCS Approval Date:</label></span>
			  <c:choose>
			  	<c:when test="${AwardBean eq null}">
			  		<span class="formfield" >--</span>
			  	</c:when>
			  	<c:otherwise>
			  		<span class="formfield"><fmt:formatDate pattern="MM/dd/yyyy" value="${AwardBean.mocsApprovalDate}"/></span>
			  	</c:otherwise>
			  </c:choose>
			  
		</div>
		<div class="row">
			  <span class="label"><label for=''>Date Sent to the Comptroller:</label></span>
			  <c:choose>
			  	<c:when test="${AwardBean eq null}">
			  		<span class="formfield" >--</span>
			  	</c:when>
			  	<c:otherwise>
			  		<span class="formfield"><fmt:formatDate pattern="MM/dd/yyyy" value="${AwardBean.dateSentToComptroller}" /></span>
			  	</c:otherwise>
			  </c:choose>
			  
		</div>
		<div class="row">
			  <span class="label"><label for=''>Registration Date:</label></span>
			  <c:choose>
			  	<c:when test="${AwardBean eq null}">
			  		<span class="formfield" >--</span>
			  	</c:when>
			  	<c:otherwise>
			  		 <span class="formfield"><fmt:formatDate pattern="MM/dd/yyyy" value="${AwardBean.registrationDate}" /></span>
			  	</c:otherwise>
			  </c:choose>
			 
		</div>
	</div>
	
	<div class='clear'>&nbsp;</div>
    </div>
    <a href="javascript:void(0);" class="exit-panel"></a> 
	
  <%-- Overlay Popup Ends --%>
