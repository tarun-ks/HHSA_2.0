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

<DIV class=""><H2>Identify an Account Administrator?</H2>
<DIV class=hr></DIV>
<DIV >
<DIV class=formcontainer>
<P>You have indicated you are not an Account Administrator for your organization. 
<div>&nbsp;</div>Please encourage the appropriate individual in your organization to go to the HHS Accelerator Portal to register for an HHS Accelerator Account. 
<div>&nbsp;</div><A id="loginredirect" name="loginredirect" class=blueLink href='http://www.nyc.gov/hhsaccelerator' title="Click here to go to the HHS Accelerator Portal now" >Click here to go to the HHS Accelerator Portal now</A> 
<P></P>
<div class=buttonholder><INPUT value=Back class="graybtutton" type=button title="Back" onClick="javascript:navigateAdminConsole();"> </div></div></div>
</DIV>
</form>

<script type=text/javascript>
var contextPathVariable = "<%=request.getContextPath()%>";
// this function is for navigation to account admin page 
function navigateAdminConsole(){
     document.myform.action=document.myform.action+'&next_action=acctAdmin&accoutRequestmodule=accoutRequestmodule';
	 document.myform.submit();

}
$(document).ready(function() {
// this function is for navigation to login page
$('#loginredirect').click(function() {
    var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
    location.href= url;
  });   
});
</script>

