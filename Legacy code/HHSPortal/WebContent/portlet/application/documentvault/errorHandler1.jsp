<h2>Your application has generated an error</h2>
<h3>Please check for the error given below</h3>
<b>Exception:</b><br> 
<%if(null != session.getAttribute("message") && null != session.getAttribute("errorMap")){%>
	<font color="red"><%=session.getAttribute("message")%></font> <br/>
   <!-- <font color="red"><%=session.getAttribute("errorMap")%></font>-->
<%}
	session.removeAttribute("message");
	session.removeAttribute("errorMap");
%>
<br>
<a href="#" title="Contact Admin">Contact Admin</a>
 
