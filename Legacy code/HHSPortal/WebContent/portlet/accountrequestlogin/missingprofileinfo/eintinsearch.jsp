<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.FaqFormBean"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>

<portlet:defineObjects />


<!--<script type="text/JavaScript" src="js/curvycorners.js"></script> -->

<form id="einsearchform" action="<portlet:actionURL/>" method ="post" name="einsearchform">

	<div id=mymain class="">
	<%
	String lsTransactionMsg = "";
	if (null!=request.getAttribute("transactionMessage")){
		lsTransactionMsg = (String)request.getAttribute("transactionMessage");
	}
	if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
		<div id="transactionStatusDiv" class="failed" style="display:block" ><%=lsTransactionMsg%> </div>
	<%}%>
	<H2>EIN / TIN Search</H2>
	<div class=hr></div>
	<div >
		<div class=formcontainer>
			<P>Search for an Employer Identification Number/Tax Identification Number (EIN/TIN) to determine if your organization already has an HHS Accelerator Account.</P><!-- Form Data Starts -->
			<div class=row><span class=label title="Please enter your EIN/TIN without dashes.">EIN/TIN:</span> <span class='required'><INPUT id = "orgEinTinNo" name = "orgEinTinNo" maxlength="10" class=input type=text></span> </div>
			<div class=buttonholder><INPUT class="graybtutton" value=Clear title="Clear" type=button onclick="javascript:clearText();">
				<INPUT class="button" value="Search" title="Search" type="submit" >
			</div>
			<!-- Form Data Ends -->
		</div>
	</div>
	<!-- Body Container Ends -->
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
</script>

<script type=text/javascript>
	 $(document).ready( function() {
	 $('#orgEinTinNo').alphanumeric( { allow: "-,.' "});
	 $("input[name='orgEinTinNo']").fieldFormatter("XX-XXXXXXX");
	 $("#einsearchform").validate({
	 		rules: {
	        	orgEinTinNo:  {
	            	required: true,
	            	minlength : 10
	            }
	        },
	        messages: {
	        	orgEinTinNo: { required: "<fmt:message key='REQUIRED_FIELDS'/>",
	        	minlength : "! Please enter EIN/TIN number with at least 9 characters."	
	        	}   
	        },
	        submitHandler: function(form){
				document.einsearchform.action=document.einsearchform.action+'&next_action=orgAcctAlreadyCreated&accoutRequestmodule=accoutRequestmodule';
				document.einsearchform.submit();
	        }
	    });
	});
	// this function is to clear the text entered in EIN number field
	function clearText(){
		document.getElementById("orgEinTinNo").value="";
	}


</script>

