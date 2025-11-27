<html xmlns="http://www.w3.org/1999/xhtml"><head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%@ page errorPage="/error/errorpage.jsp" %>

<title>NYC_Human Health Services Accelerator</title>

<div class=bodycontainer><H2>Account Request Submitted</H2>
<div class=hr></div>
<div class=container>
<div class=formcontainer>
<P>Your NYC.ID account request has been submitted. <BR>An activation email with a validation link will be sent to <A class=blueLink href="#" >request.getAttribute("emailAddr") != null ? request.getAttribute("emailAddr"): ""</A> to activate this account. <BR><BR><A id="loginredirect" href="#" title="Click here to return to the HHS Accelerator Portal">Click here to return to the HHS Accelerator Portal</A> </P><BR></div></div><!-- Body Container Ends -->


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
	$(document).ready(function() {
		//this function is for navigation to login page
		$('#loginredirect').click(function() {
			   var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
			   location.href= url;
		});  
	});
</script>
