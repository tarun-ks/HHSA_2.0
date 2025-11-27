<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<div class="hhs_header">
	<h1>Human Health Services Accelerator</h1>
    
	<!-- toolbar start -->
	<div class="toolbar">
		<div class="textresize" style="font-size:12px;">Text Size:
		    <ul>
		        <li><a onclick="changemysize(this, 10);" href="javascript:void(0);"  title="Small Text Size" id="smallA">A</a></li>
		        <li><a onclick="changemysize(this, 12);" href="javascript:void(0);"  title="Medium Text Size" id="mediumA" class="textmedium">A</a></li>
		        <li><a onclick="changemysize(this, 14);" href="javascript:void(0);"  title="Large Text Size" id="largeA" class="textbig">A</a></li>
		    </ul>
		    <input type='hidden' name='aaaValueToSet' id='aaaValueToSet' value='${aaaValueToSet}' />
		    <input type='hidden' name='urlForAAA' id='urlForAAA' value='${pageContext.servletContext.contextPath}/saveAAASize?next_action=aaaValueToSet' />
		</div>
	</div>
	<!-- toolbar ends -->
</div>

<div ><H2>Account Request Submitted</H2></div>
<div class=hr></div>
<div class=container>
	<div class=formcontainer>
		<P>Your NYC.ID account request has been submitted. <BR>An activation email with a validation link will be sent shortly to <b><%=renderRequest.getAttribute("emailAddress") %></b> to activate this account. <BR><BR><A id="loginredirect" class=blueLink href="#">Click here to return to the HHS Accelerator Portal</A> </P><BR>
	</div>
</div>

<script type=text/javascript>
var contextPathVariable = "<%=request.getContextPath()%>";
	//jquery ready function- executes the script after page loading
$(document).ready(function() {
	
	//jquery click method call that logouts the current user and returns user to hhs login page
	$('#loginredirect').click(function() {
		
		//var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_login_page&_nfls=false&logout=logout&app_menu_name=logout_icon" ;
		var url = "http://www.nyc.gov/hhsaccelerator";
		location.href= url;
	});  
});
</script>
