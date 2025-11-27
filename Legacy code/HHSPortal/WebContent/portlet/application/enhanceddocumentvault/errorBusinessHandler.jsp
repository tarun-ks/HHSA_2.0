<html>
<head>
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">
</head>
<body>
<div class="messagediv" id="messagediv" style="width:50%"></div>
		<script type="text/javascript">
			  $(".messagediv").html('<%= request.getAttribute("message")%>');
			  $(".messagediv").addClass('<%= request.getAttribute("messageType")%>');
			  $(".messagediv").show();
		</script>	
</body>
</html>