<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<style>
	.failed {
	     display:block;
	}
	.passed{
	 display:block;
	}
</style>
<script type="text/javascript">
    var serverName = "<%=request.getServerName()%>";
    var userType = "<%=request.getAttribute("user_type")%>";
    var siteminderLogout = "<%=request.getAttribute("siteminderLogout")%>";
    var siteminderError = "<%=request.getAttribute("siteminderLoginError")%>";
	var contextPathVariable = "<%=request.getContextPath()%>";
	var userRole = "<%=session.getAttribute("role")%>";
	if(userType!=null && userType!=""){
		var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_city_home&_nfls=false";
	if("agency_org"==userType){
		if(userRole!=null && userRole.toLowerCase()=='staff' || userRole.toLowerCase()=='manager'){
			url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_agency_r1&_nfls=false";
		}else{
			url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_agency_home&_nfls=false";	
		}
	}
	       location.href= url;
  }else if(siteminderLogout!=null && siteminderLogout!=""){
    delete_Cookie("SMSESSION","/",serverName);
    var url =contextPathVariable+"/portal/hhsweb.portal";
    if(siteminderError!=null && siteminderError!=""){
    	url =contextPathVariable+"/portal/hhsweb.portal?displayError=displayError";
    }
	 location.href= url;
  }
  
 //This function deletes the SMSESSION Cookie 
function delete_Cookie( name, path, domain ) 
{
   document.cookie=name+"="+((path) ? ";path="+path:"")+((domain)?";domain="+domain:"") +
                                   ";expires=Thu, 01 Jan 1971 00:00:01 GMT";
}
</script>

	