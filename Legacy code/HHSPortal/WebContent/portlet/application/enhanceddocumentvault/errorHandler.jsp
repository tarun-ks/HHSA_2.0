<html>
<head>
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">
</head>
<body>
|<%=request.getSession().getAttribute("type")%>|<%=request.getSession().getAttribute("isLinkedToAPP")%>|<%=request.getSession().getAttribute("message")%>|<%=request.getSession().getAttribute("messageType")%>|</body>
<%
	request.getSession().removeAttribute("type");
	request.getSession().removeAttribute("isLinkedToAPP");
	request.getSession().removeAttribute("message");
	request.getSession().removeAttribute("messageType");
%>
</html>