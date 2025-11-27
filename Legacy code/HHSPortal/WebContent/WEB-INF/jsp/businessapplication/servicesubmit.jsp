<!-- This page is displayed when user submit the single service application by clicking on the submit tab at the top.
It will display terms and conditions page and user can submit the single service-->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>   

<portlet:defineObjects/>


<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">
    .commentHidden{
		display:none;
	}
	</style>

		
<script type=text/javascript>
function showMe (it, box) {
	var vis = (box.checked) ? "block" : "none";
	document.getElementById(it).style.display = vis;
	// code to enable Submit on click of Enter key
    if(document.getElementById("acceptakg").checked == true){
    	if (document.layers)
    	    document.captureEvents(Event.KEYDOWN);
    	document.onkeydown =
    	    function (evt) {
    	        var keyCode = evt ? (evt.which ? evt.which : evt.keyCode) : event.keyCode;
    	        if (keyCode == 13) { 
    	            saveForm('applicationSubmit');
    	        }
    	        else
    	            return true;
    	    };
    }
} 

function getmycookie(myname)
// this function is called by the function mydefaultsize()
// this function merely looks for any previously set cookie and then returns its value
{
	// if any cookies have been stored then
	if (document.cookie.length>0)
	  {
		  // where does our cookie begin its existence within the array of cookies  
		  mystart=document.cookie.indexOf(myname + "=");
		  // if we found our cookie name within the array then
		  if (mystart!=-1)
		    {
				// lets move to the end of the name thus the beginning of the value
			 	// the '+1' grabs the '=' symbol also
			    mystart=mystart + myname.length+1;
				// because our document is only storing a single cookie, the end of the cookie is found easily
			    myend=document.cookie.length;
				// return the value of the cookie which exists after the cookie name and before the end of the cookie
			    return document.cookie.substring(mystart,myend);
		    }
	  }
	// if we didn't find a cookie then return nothing  
	return "";
}

function mydefaultsize(){
	// this function is called by the body onload event
	// this function is used by all sub pages visited by the user after the main page
	var div = document.getElementById("mymain");
	// call the function getmycookie() and pass it the name of the cookie we are searching for
	// if we found the cookie then
		if (getmycookie("mysize")>0)
		{
			// apply the text size change	
			div.style.fontSize = getmycookie("mysize") + "px";
		}
}

function enablebutton(){
	var e=document.getElementById("acceptakg");
	if(e.checked == true){
		document.getElementById("start").disabled=false;
  	} 
  	else{
		document.getElementById("start").disabled=true;
  	}
}
function saveForm(nextaction) {
	document.myform.action = document.myform.action+"&business_app_id="+'<%=renderRequest.getAttribute("business_app_id")%>'
			+"&service_app_id="+'<%=renderRequest.getAttribute("service_app_id")%>'
			+ '&next_action=' + nextaction + "&section="
			+ '<%=request.getAttribute("section")%>'+"&subsection="+'<%=request.getAttribute("subsection")%>';
	document.getElementById("start").disabled=true;
	document.myform.submit();
}
<c:forEach var="map1" items="${loBusinessStatusBeanMap}">
	setStatusSection("${map1.key}", "${map1.value.msSectionStatus}");
</c:forEach>
</script>

<style>
.individualError {
    float: none;
}
</style>

<div class="containerpanel">
	<p></p>
	<h2>HHS Accelerator Application Submission Terms and Conditions</h2>
	
	<div class='floatRht'>	
	<c:if test="${org_type eq 'provider_org'}">
	<c:if test="${serviceComments ne null and ! empty serviceComments}">
							<%@include file="showServiceCommentsLink.jsp" %>
							 <div class="commentHidden" style="padding:10px;">
								<c:forEach var="loopItems" items="${serviceComments}" varStatus="counter">
								     	<c:if test="${counter.index ne 0}">
								     	 -------------------------------------------------<br>
								     	</c:if>
							     <b>${loopItems['USER_ID']} - <fmt:formatDate pattern="MM/dd/yyyy" value="${loopItems['AUDIT_DATE']}" /></b><br>
							      ${loopItems['DATA']}	<br>
						     </c:forEach>
					     </div>
					</c:if>
			</c:if>
				</div>
	<div class="clear"></div>
	<!-- Form Data Starts -->


	<form name="myform" action="<portlet:actionURL/>" method ="post"  >

	<div id="mymain">
	<%
		if(request.getAttribute("errorMsg") != null){
	%>
	<div class="notify"> 
		<div><span class="individualError"><%=request.getAttribute("errorMsg")%></span></div>
	</div>
	<%
		}
	%>	
	<div class="termspanel clear">
				<div>
		 	${lsDisplayTermsCondition}
		 	</div>
	</div>	
	<input type="hidden" name="action_redirect" value="${next_action}" />
	<div class="holder">
		<p><input name="terms" type="checkbox" onclick="showMe('esignature', this)" id="acceptakg"/>I have read the terms and conditions and have reviewed the <a style='text-decoration: underline;color: #5077AA;' href="javascript:viewDocumentByType('Standard Contract');">Standard Contract</a> and <a href="javascript:viewDocumentByType('Appendix A');" style='text-decoration: underline;color: #5077AA;'>Appendix A - General Provisions Governing Contracts for Consultants, Profession, Technical, Human and Client Services</a></p>
		<div style="display:none" id="esignature">
			<h4>Submit Application E-Signature</h4>
			<div class="formcontainer">
		 		<div class="row">
		      		<span class="label">User Name:</span>
		      		<span class="formfield"> <input class="input" type="text" name="userName" autocomplete="off"/></span>
		  		</div>
			  	<div class="row">
			      <span class="label">Password:</span>
			      <span class="formfield"> <input class="input" type="password" name="password" autocomplete="off"/></span>
			  	</div>
		 		<div class="buttonholder">
		 		<input id='start' type="submit" title="Submit Application" onclick="saveForm('applicationSubmit')" class="button" value="Submit Application" /></div>
		 	</div>
 		</div>
	</div>
	</form>
<!-- Form Data Ends -->
</div>

