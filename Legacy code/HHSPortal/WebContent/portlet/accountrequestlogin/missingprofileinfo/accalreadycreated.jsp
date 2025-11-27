<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.FaqFormBean"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.*"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


 
<portlet:defineObjects />

<title>NYC_Human Health Services Accelerator</title>

<form id="myform" action="<portlet:actionURL/>" method ="post" name="myform">

	<DIV >
		<%
		String lsTransactionMsg = "";
		if (null!=request.getAttribute("transactionMessage")){
			lsTransactionMsg = (String)request.getAttribute("transactionMessage");
		}
		
		String lsEinNo="";
		if(null!=request.getAttribute("einNumber")){
			lsEinNo = (String)request.getAttribute("einNumber");
		}
		 if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
		     <div id="transactionStatusDiv" class="failed" style="display:block" ><%=lsTransactionMsg%> </div>
		<%}%>
 		<H2>Organization Account Already Created</H2>
		<DIV class=hr></DIV>
		<DIV>
			<DIV class=formcontainer>
				<P>Your Organization already has an HHS Accelerator account. <BR>Would you like to request an account for the system from your organization's HHS Accelerator Account Administrator? </P>
				<div class=row><span class=label >EIN/TIN:</span> <span class='required'><INPUT id = "orgEinTinNo" name = "orgEinTinNo" value="<%=lsEinNo%>" maxlength="10" class=input readonly="readonly" type=text></span> </div>
				<DIV class=buttonholder><INPUT value=No class="graybtutton" type=button title="No" onClick="javascript:navigateAdminConsole();">
					<INPUT value="Yes, request an account" type=button title="Yes, request an account" onClick="javascript:navigateToAcctReqSubmitted();">
				</DIV>
			 </DIV>
		 </DIV>
	 
		 <!-- Body Container Ends -->
	</DIV>
</form>

<script type=text/javascript>
var contextPathVariable = "<%=request.getContextPath()%>";
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
//this function is for navigation to account request submitted page
function navigateToAcctReqSubmitted(){
	 pageGreyOut();
     document.myform.action=document.myform.action+'&next_action=userAcctReqSubmitted&accoutRequestmodule=accoutRequestmodule';
	 document.myform.submit();
	 removePageGreyOut();
}

//this function is for navigation to login page
function navigateAdminConsole(){
	//var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
	 var redirectWindow = window.open('http://www.nyc.gov/hhsaccelerator', '_blank');
    redirectWindow.location;
	var url = contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&amp;_pageLabel=portlet_hhsweb_portal_login_page&amp;_nfls=false&amp;logout=logout";
    location.href= url;
}

</script>