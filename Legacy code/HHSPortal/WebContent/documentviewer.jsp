<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.io.InputStream" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title><%=request.getParameter("documentName")%></title>
</head>
<body bgcolor="#C0CACF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<script type="text/javascript">
	//this JavaScript sets the width and height of viewONE for all browsers
	var archiveTag = 'ARCHIVE="viewone.jar"';
	var sizeTag;	
	var appletHeight;
	var appletWidth;
	
	if (navigator.userAgent.toLowerCase().indexOf("mac_") > 0 || (navigator.userAgent.toLowerCase().indexOf("macintosh")> 0 && navigator.userAgent.toLowerCase().indexOf("ozilla")> 0))
	{
		archiveTag = 'ARCHIVE="viewonedsa.jar"';
	}
	appletHeight = 1000;
	//appletWidth = 800;	
	appletWidth = 1200;
	
	sizeTag = 'WIDTH="' + appletWidth + '" HEIGHT="' + appletHeight + '"';
	onerror=errorHandler;
	
	function errorHandler()
	{
	//if we get here it is probably because a call has been made
	//to the applet before the browser has had time to initializew it
	//it can therefore be ignored
		showErrorMessagePopup();
		removePageGreyOut();
	}
	
	
    // alert("<%=request.getParameter("documentName")%>");
	
	//alert("<%=request.getParameter("documentType")%>");
	
	
</script>
<table width="100%" height="100%" border="1" align="center" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
  <tr>
    <td valign="top">
      <table width="100%" border="1" cellspacing="0" cellpadding="0">
        <tr>
          <td width="100%" valign="top">
            <table width="100%"  border="1" cellspacing="0" cellpadding="0">
              <tr>
                <td width="1000" height="800" valign="top">
                  <p class="head">
                    <script type="text/javascript">
						document.write('<applet CODEBASE="v1files"');
						document.write(archiveTag); 
						document.write('CODE="start.jiViewONE.class" NAME="viewONE" ID="viewONE"'); 
						document.write(sizeTag);
						document.write('HSPACE="0" VSPACE="0" ALIGN="middle" mayscript="true">');
						//document.write('HSPACE="0" VSPACE="0" ALIGN="topleft" mayscript="true">');
						document.write('<param name="cabbase" value="viewone.cab">');
						document.write('<param name="demo" value="document">');
						document.write('<param name="flipOptions" value="true">');
						document.write('<param name="scale" value="ftow">');
						document.write('<param name="viewmode" value="thumbsleft">');
						document.write('<param name="pagekeys" value="true">');
						document.write('<param name="defaultprintdoc" value="true">');
						document.write('<param name="externalMagnifier" value="false">');
						document.write('<param name="allowTextFind" value="true">');
						document.write('<param name="enhance" value="true">');
						document.write('<param name="enhancemode" value="1">');
						document.write('<param name="version3Features" value="true">');
						document.write('<param name="annotationEncoding" value="UTF8">');
						document.write('<param name="annotate" value="true">');
						document.write('<param name="annotateEdit" value="true">');
						document.write('<param name="obfuscateUV" value="false">');
						document.write('<param name="fileButtonSave" value="true">');
						// added the below parameter for displaying the filename with extension in save dialog box 
						document.write('<param name="defaultSaveFilename" value="<%=request.getParameter("documentName")+ "." +request.getParameter("documentType") %>">');
						document.write('<param name="printButtons" value="false">');
						document.write('<param name="annotate" value="false">');
						document.write('<param name="textFilterWrapping" value="true">');
						document.write('<param name="textFilterPageWidth" value="194">'); // should always be more than 194, else will face issues
						document.write('<param name="textFilterPageHeight" value="120">'); // can be varied as per requirements						 
						document.write('<param name="trace" value="true">');
						document.write('<param name="tracenet" value="true">');
						document.write('<param name="tracefilter" value="true">');
						document.write('<param name="filename" value="/HHSPortal/GetContent.jsp;jsessionid=<%=request.getSession().getId()%>?getDocument=<%=request.getParameter("documentId")%>">');						
						document.write('</applet>');
            		</script>
                  </p>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</body>
</html>