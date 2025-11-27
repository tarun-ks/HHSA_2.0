<%@ page language="java" contentType="text/html; charset=ISO-8859-1"     pageEncoding="ISO-8859-1"%>
<%@page  import="java.util.*"%>
<%@page  import= "weblogic.security.Security" %>
<%@page  import= "javax.security.auth.Subject" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<h2>Welcome "<%=request.getRemoteUser()%>" to <B><%=request.getSession().getServletContext().getServletContextName()%></B> <B>protected page</B> </h2>

<br/>
<br/>

<h1>Test page .....</h1>
<br/>
<br/>

</body>
</html>