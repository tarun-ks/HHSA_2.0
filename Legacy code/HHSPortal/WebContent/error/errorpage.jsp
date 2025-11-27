<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isErrorPage="true" import="java.io.*" %>
<html>
<head>
	<title>Exceptional Event Occurred!</title>
	<style>
	body, p { padding-left:30 }
	pre { font-size:8pt }
	</style>
</head>
<body>

<%-- Exception Handler --%>
<%if(request.getAttribute("invalidCharacters")!=null){ %>
<h2>Your request contains hazardous character sequence.</h2>
<%}else{ %>
<h2>An error has occurred in this page.</h2>
<%} %>



</body>
</html>


