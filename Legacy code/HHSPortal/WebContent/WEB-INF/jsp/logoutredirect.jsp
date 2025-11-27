<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.util.HHSUtil"%>
<%@ page import="com.nyc.hhs.constants.ApplicationConstants"%>
<!-- Start QC 4989 R 8.4.0 Incorrect Copyright year in Accelerator Logout page with Reconnect button - obtain year dynamically -->
<%@ page import="com.nyc.hhs.util.CommonUtil"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%
	Date loTodaysDate = new Date();
	Calendar loCalendar = Calendar.getInstance();
	loCalendar.setTime(loTodaysDate);
	// Start qc -9642 The SD Element id T1539 - Clear browser data on user logout
	//headers
	response.setHeader( "Clear-Site-Data", "*" );//Clear browser data
	//End qc -9642 The SD Element id T1539 - Clear browser data on user logout
	
	String errorMsg = request.getParameter("msg") == null ? "no" : (String)request.getParameter("msg");
	
%>
<!-- End QC 4989 R 8.4.0 Incorrect Copyright year in Accelerator Logout page with Reconnect button - obtain year dynamically -->
<!DOCTYPE html>
<html>
<head>
    <title>Health and Human Services Accelerator</title>
    <meta charset="utf-8">    
    <meta name="keywords" content="HTML, CSS">
    <meta name=viewport content="width=device-width" initial-scale=1.0>
	<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE11,IE=edge,chrome=1">
	<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7; IE=EDGE">
	<meta http-equiv="Clear-Site-Data" content="*">
	<meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
	<!-- <meta http-equiv="X-Frame-Options" content="allow" /> -->
    <link rel=stylesheet href="${pageContext.servletContext.contextPath}/css/css.css">
    <link rel=stylesheet href="${pageContext.servletContext.contextPath}/css/static.css">
    <style>
		a:link {
		  color: blue;
		  font-size: 12px; 
		  text-decoration: underline;
		}

		a:visited {
		  color: blue;
		}

		a:hover {
		  color: red;
		}

		a:active {
		  color: blue;
		}
</style>
</head> 

<%-- QC 9485 R 8.4.0 do not use iframe
<% 
    String logoutUrl = HHSUtil.logoutSAMLcity( request );
    String hostUrl = HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGIN_STATIC_CITY);   
    String iframeUrl=logoutUrl + "?x-frames-allow-from=" + hostUrl;
    request.getSession().invalidate();
%>
--%>

<body>

<!-- Start  QC 9485 R 8.4.0 do not use iframe and spinner -->
<!-- 
<iframe id="idFrame" src="" style="display: none" ></iframe>  
-->

 <!--
 <script type="text/javascript">
 
    function showButton() {
      document.getElementById("progressImg").style.visibility = "hidden";
      document.getElementById("reconnectButton").style.visibility = "visible";
      
    }
  --> 
   <%--  document.getElementById("idFrame").src = "<%=iframeUrl%>";  --%>
   <!--
    // adjust this as needed, 1 sec = 1000
     setTimeout("showButton()", 5000); 
  
   
</script>
 //End  QC 9485 R 8.4.0 do not use iframe and spinner
--> 

 
<div id="main-wrapper">    
<div id="mymain" class="bodycontainer" style="font-size: 14px;">
    <div class="wlp-bighorn-theme wlp-bighorn-theme-borderless"></div>
    <div class="container">
        <div class="wlp-bighorn-layout wlp-bighorn-layout-flow">
            <div class="wlp-bighorn-layout-cell wlp-bighorn-layout-flow-horizontal wlp-bighorn-layout-flow-first" style="width: 100%">
                <div></div>
                <div id="portletInstance_7" class="wlp-bighorn-window  ">
                    <div class="wlp-bighorn-window-content">
            
  
                    <div class="nycidm-header">
        
		              <div class="upper-header-black">
			             <div class="container-nycidm">
				            <span class="upper-header-left"> 
                               <img class="small-nyc-logo" alt="" src="${pageContext.servletContext.contextPath}/img/nyc_white.png">
					           <img class="vert-divide" alt="" src="${pageContext.servletContext.contextPath}/img/upper-header-divider.gif">	
					           <span class="upper-header-black-title">
                    	           HHS Accelerator
                	           </span>
				            </span> 
							
				            <img class="vert-divide-right" alt="" src="${pageContext.servletContext.contextPath}/img/upper-header-divider.gif">
                             
			             
			             </div>
                    </div>
                </div>
            
            <div class="hhs_header">
	           <h1>Human Health Services Accelerator</h1>
	       </div> 
		 
              <div class="hrBold"></div>
                        
            <%-- QC9713 --%>
			<%if("no".equals(errorMsg)){ %>
			<p>You are successfully logged out.</p>
			<p>Click the "Reconnect" button to log into HHS Accelerator.</p>
			<br>
			<br>
			<br>
			<center>
				<div>
					<!-- QC 9485 R 8.4.0 show button  
   <input  type="button" value="Reconnect" id="reconnectButton" class="graybtutton"  title="Reconnect" onclick="location.href='/HHSPortal/portal/hhsweb.portal'"  style="align-items:center; visibility: hidden">                     
   -->
					<input type="button" value="Reconnect" id="reconnectButton" class="graybtutton" title="Reconnect" onclick="location.href='/HHSPortal/portal/hhsweb.portal'" style="align-items: center; visibility: visible">
				</div>
				<!-- QC 9485 R 8.4.0 do not show spinner 
 <img id="progressImg" src="${pageContext.servletContext.contextPath}/img/progress.gif" style="visibility: visible;">    
 -->
			</center>
			<%} else if("mul".equals(errorMsg)) {%>
			<div>								
			<br/><br/>								
		<center>				
			  <h2 style="text-align:center;font-size:160%;color:black;">You have been logout from the system!</h2>
		</center>									
			<br/><br/><br/><br/>							
				<i>HHS Accelerator is unable to recognize your user role.<br> For proper updates, please submit a request via our&nbsp;<a href='https://mocssupport.atlassian.net/servicedesk/customer/portal/8'>MOCS Service Desk</a>&nbsp;contact form.  In the form, select <b>HHS Accelerator </b>then <b> Agency Provisioning </b> and include the following information:</i><br> (1) Full Name <br> (2) Title <br> (3) Agency <br> (4) User role for your organization <br/><br/> <i>If you are unsure what your user role should be in HHS Accelerator, before submitting your request, please contact the MOCS Liaison for your agency.</i>
				<br><br><br><p />													
			</div>
			<%} else if("dep".equals(errorMsg)) {%>
			<div><br><br><br><p /><p />									
				<%= ApplicationConstants.SAML_DEPROVISION_USER_ERR %>									
			</div>
			<%} %> 				
            </div>     
            </div>   
        </div>   
    </div>   
</div> 
</div>
<div class="footer">
 <!-- Start  QC 4989 R 8.4.0 Incorrect Copyright year in Accelerator Logout page with Reconnect button - obtain year dynamically -->
<!--	<div class="copyright">Copyright 2020 The City of New York</div>  -->
	<div class="copyright">Copyright <%=loCalendar.get(Calendar.YEAR)%> The City of New York
		<% if (null!=CommonUtil.buildConstant() && !"".equalsIgnoreCase(CommonUtil.buildConstant())){ %>
		   <label><%=CommonUtil.buildConstant()%></label>
		  <%}%>
	</div>
 <!-- End  QC 4989 R 8.4.0 Incorrect Copyright year in Accelerator Logout page with Reconnect button - obtain year dynamically -->	
	<div class="fotterlinks">
		<ul>
			<li><a href="http://www.nyc.gov/faqs" target="_blank" title="FAQ">FAQ</a></li>
			<li><a href="http://www.nyc.gov/privacy" target="_blank" title="Privacy Statement">Privacy Statement</a></li>
			<li class="nobdr"><a href="http://www.nyc.gov/sitemap" target="_blank" title="Site Map">Site Map</a></li>
		</ul>
	</div>
</div>

    
</div>   
</body>    
 
</html>