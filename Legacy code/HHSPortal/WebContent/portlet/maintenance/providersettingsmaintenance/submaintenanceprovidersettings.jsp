<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<portlet:defineObjects />
   	<div id ="userDetailsTempContainer">
   		<div class="formcontainer">
		   	<div class="row">
			      <span class="label">First Name:</span>
			      <span id="" class="formfield selectedservice"><input type="text" style="width:75%;" id="fname" value="${fname }" disabled="disabled"></span>
			</div>
			<div class="row">
			      <span class="label">Middle Initial:</span>
			      <span id="" class="formfield selectedservice"><input type="text" size="1" style="width:8%;" id="mname" value="${mname }" disabled="disabled"></span>
			</div>
			<div class="row">
			      <span class="label">Last Name:</span>
			      <span id="" class="formfield selectedservice"><input type="text" style="width:75%;" id="lname" value="${lname }" disabled="disabled"></span>
			</div>
			<input type="hidden" id="existingProviderList" value="${existingProviderList}">   
		</div>
		<p style="text-align:left;">
			<span style="font-family:Verdana;font-size:12px;font-weight:normal;font-style:normal;text-decoration:none;color:#000000;">
				The table below displays organizations that this NYC.ID currently has access to and organizations with pending access requests.
			</span>
		</p>
		<!-- Provider Details Grid -->
		<div class="tabularWrapper">
			<st:table  objectName="staffDetailsBeanList"  cssClass="heading" alternateCss1="evenRows" alternateCss2="oddRows">
				<st:property headingName="Provider Name" columnName="msOrganisationName"
					align="left" size="30%" />			
				<st:property headingName="Office Title" columnName="msOfficeTitle"
					align="right" size="25%" />
				<st:property headingName="Permission Level" columnName="msPermissionLevel"
					align="right" size="35%" />
			</st:table>
			<c:if test="${staffDetailsBeanList eq null or fn:length(staffDetailsBeanList) eq 0}">
				<div class="messagedivNycMsg noRecord" id="messagedivNycMsg">&nbsp;</div>
			</c:if>
			<div>&nbsp;</div>
		</div>
   	</div>
		
