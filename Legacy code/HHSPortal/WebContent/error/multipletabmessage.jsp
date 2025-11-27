<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.util.CommonUtil"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%
	Date loTodaysDate = new Date();
	Calendar loCalendar = Calendar.getInstance();
	loCalendar.setTime(loTodaysDate);
		
%>

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
	<!-- <meta http-equiv="X-Frame-Options" content="allow" /> -->
    <link rel=stylesheet href="${pageContext.servletContext.contextPath}/css/css.css">
    <link rel=stylesheet href="${pageContext.servletContext.contextPath}/css/static.css">
    
</head> 


<body>


 
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
               
			    <p>The page you're trying to view is already open in another browser tab or another browser. Please close this tab/browser to proceed.</p>
                <br><br><br>
              
            </div>
     
            </div>   
        </div>   
    </div>   
</div> 
</div>
<div class="footer">

	<div class="copyright">Copyright <%=loCalendar.get(Calendar.YEAR)%> The City of New York
		<% if (null!=CommonUtil.buildConstant() && !"".equalsIgnoreCase(CommonUtil.buildConstant())){ %>
		   <label><%=CommonUtil.buildConstant()%></label>
		  <%}%>
	</div>

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