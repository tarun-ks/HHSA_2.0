<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.FaqFormBean"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.*"%>
<%@page import="com.nyc.hhs.frameworks.grid.*"%>
<%@page import="com.nyc.hhs.util.CommonUtil"%>

<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<portlet:defineObjects />

<title>NYC_Human Health Services Accelerator</title>

<form id="myform" action="<portlet:actionURL/>" method ="post" name="myform">
<!--[R9.7.7 QC9736]  -->
<!-- 	<div class="">
		<H2>Are you an Account Administrator?</H2>
		<div class=hr></div>
		<div class=container>
			Only an Account Administrator can register for an organization's HHS Accelerator account.<BR>The Account Administrator will be responsible for user account maintenance in the system.<BR>Once your organization is registered, you may also designate other users as Account Administrators.<BR>
			<div>&nbsp;</div>
			<div class=buttonholder><input value=No type=button title="No"  class="graybtutton" onClick="javascript:navigateToAdminAcctIdentify();">
				<input value=Yes type=button title="Yes" onClick="javascript:navigateToCreateOrgAcct();">
			</div>
		 </div>
	 </div>
	 Body Container Ends
	</div>
	</div>
 -->
	<div class="">
		<H2>Organization Account Request</H2>
		<div class=hr></div>
		<div class=container>
			<%=CommonUtil.getOrgAccountRequestDiableNote()%>
			<div>&nbsp;</div>
		 </div>
	 </div>
	 <!-- Body Container Ends -->
	</div>
	</div>

</form>
</form>

<script type=text/javascript>

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
//this function is for navigation to admin account identification page
function navigateToAdminAcctIdentify(){
     document.myform.action=document.myform.action+'&next_action=acctAdminIdentification&accoutRequestmodule=accoutRequestmodule';
	 document.myform.submit();
}
//this function is for navigation to create organization account page
function navigateToCreateOrgAcct(){
     document.myform.action=document.myform.action+'&next_action=createAdminAcct&accoutRequestmodule=accoutRequestmodule';
	 document.myform.submit();
}
</script>
