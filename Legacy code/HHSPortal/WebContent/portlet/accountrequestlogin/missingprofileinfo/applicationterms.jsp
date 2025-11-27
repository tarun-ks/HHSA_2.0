<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.ApplicationConstants" %>
<portlet:defineObjects/>

<!-- $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ TEXT RESIZE CODE $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ -->		
<script type=text/javascript>
	function showMe (it, box) {
		var vis = (box.checked) ? "block" : "none";
		document.getElementById(it).style.display = vis;
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
	
	<% 
	String lsTermsAndConditions="";
	 if(renderRequest.getAttribute("lsDisplayTermsCondition")!=null){
	 	lsTermsAndConditions = (String)renderRequest.getAttribute("lsDisplayTermsCondition");
	 }
	%>
	
	//this function is to enable the continue button when user accept the terms and condition
	function enablebutton(){
		var e=document.getElementById("acceptakg");
		if(e.checked == true){
			document.getElementById("start").disabled=false;
		} 
		else{
			document.getElementById("start").disabled=true;
		}
	}
	//this function is for navigation to terms and conditions page
	function acceptTermsConditions(){
	
	     document.myform.action=document.myform.action+'&next_action=einTinSearch&accoutRequestmodule=accoutRequestmodule';
		 document.myform.submit();
	}
</script>
<!-- Added for R4: Adding header specially for TnCs in case of User with Multiple Organizations -->
    <%
	if(session.getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG) == null){
	%>
<div class="hhs_header">
	<h1>Human Health Services Accelerator</h1>
	    
	<!-- toolbar start -->
	<div class="toolbar">
		<div class="textresize">Text Size:
		   <ul>
				        <li><a onclick="changemysize(this, 10);" href="javascript:void(0);"  title="Small Text Size" id="smallA">A</a></li>
				        <li><a onclick="changemysize(this, 12);" href="javascript:void(0);"  title="Medium Text Size" id="mediumA" class="textmedium">A</a></li>
				        <li><a onclick="changemysize(this, 14);" href="javascript:void(0);"  title="Large Text Size" id="largeA" class="textbig">A</a></li>
				    </ul>
				    <input type='hidden' name='aaaValueToSet' id='aaaValueToSet' value='${aaaValueToSet}' />
				    <input type='hidden' name='urlForAAA' id='urlForAAA' value='${pageContext.servletContext.contextPath}/saveAAASize?next_action=aaaValueToSet' />
		</div>
		<ul id="toolsIconUlID" class='toolbarIcons emptyClass'>
					<li id="home_icon" class="homeicon active"><a href="javascript:void(0)" >Home</a>
					</li>
				     <li id="logout_icon" class="logouticon"><a  href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" title="Log Out">Logout</a></li>
				</ul>
	</div>
	<!-- toolbar ends -->
</div>
	<div class="breadcrumb">
<%if(null != session.getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME)){ %>
				<div class="logininfo"><span class="bold">Welcome:</span>
					<span id=''><%=session.getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME)%></span>
				</div>
			<%} %>
		</div>
<%}%>
<div class="containerpanel">
	<h2>HHS Accelerator Terms and Conditions</h2>
	<!-- Form Data Starts -->
	
	
	<form name="myform" action="<portlet:actionURL/>" method ="post"  >
	
	<div id="mymain">
		<%
			String lsTransactionMsg = "";
			if (null!=request.getAttribute("transactionMessage")){
				lsTransactionMsg = (String)request.getAttribute("transactionMessage");
			}
			if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
			    <div id="transactionStatusDiv" class="failed" style="display:block" ><%=lsTransactionMsg%> </div>
			<%}else{%>
				
		<div class="termspanel clear">
			<%=lsTermsAndConditions %>
		</div>
		<div class="holder">
			<div class="left">
				<input name="terms" type="checkbox" onclick="enablebutton();" id="acceptakg"/>I agree to the Terms and Conditions
			</div>
			<div class="right">
				 <input id='start' type="submit" class="button" value="Continue" title="Continue" onclick="javascript:acceptTermsConditions();" disabled="disabled" style="float:right;"/>
			</div>
		</div>
	<%} %>
	</div>

</form>
<!-- Form Data Ends -->
</div>
