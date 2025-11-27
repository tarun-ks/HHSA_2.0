<%@page import="com.nyc.hhs.constants.ApplicationConstants" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%  
String env = System.getProperty("hhs.env");	
String userOrgType = (String) session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);		
System.out.println("evn -> " + env + ", userOrgType -> " + userOrgType);	
if(env != null && !env.isEmpty() && userOrgType != null && !userOrgType.isEmpty()) {
  if(userOrgType.equals(ApplicationConstants.CITY_ORG) || userOrgType.equals(ApplicationConstants.AGENCY_ORG)){
   if(env.equalsIgnoreCase("prd")){%>		    	   
<script async src="https://www.googletagmanager.com/gtag/js?id=G-0211JS0XW0"></script>
 <script>
   window.dataLayer = window.dataLayer || [];
   function gtag(){dataLayer.push(arguments);}
   gtag('js', new Date());
   gtag('config', 'G-0211JS0XW0');
</script>
<%}else if(env.equalsIgnoreCase("ts2") || env.equalsIgnoreCase("stg") || env.equalsIgnoreCase("local")){%>
<script async src="https://www.googletagmanager.com/gtag/js?id=G-RX85E7KWD4"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());
  gtag('config', 'G-RX85E7KWD4');
</script>
<%}%>
</script>					
<%
 } else if (userOrgType.equals(ApplicationConstants.PROVIDER_ORG)){
    if(env.equalsIgnoreCase("prd")){
%>
<script async src="https://www.googletagmanager.com/gtag/js?id=G-L6C26JWTQ3"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());						
  gtag('config', 'G-L6C26JWTQ3');
</script>
<%}else if(env.equalsIgnoreCase("ts2") || env.equalsIgnoreCase("stg") || env.equalsIgnoreCase("local")){%>
<script async src="https://www.googletagmanager.com/gtag/js?id=G-RX85E7KWD4"></script>
<script>
 window.dataLayer = window.dataLayer || [];
 function gtag(){dataLayer.push(arguments);}
 gtag('js', new Date());						
 gtag('config', 'G-RX85E7KWD4');
</script>
   <%}						
  }		    		  
}%>