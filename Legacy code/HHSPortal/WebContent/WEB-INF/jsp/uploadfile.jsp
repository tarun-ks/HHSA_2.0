<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<html>
	<head>
		<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<title>Upload Example</title>
	</head>
	<body>
		<h2>Upload Document</h2>
		<hr/>
		Select a document category and document type, then
				browse for the file to upload.
		<br/>
		<form action="<portlet:actionURL/>" enctype="multipart/form-data" method="post">
		<table>
			<tr>
				<td>Document Category:</td>
				<td><select name="doccategory" style="width: 300px">
					<option></option>
					<option value="Audit">Audit</option>
					<option value="Corporate Structure">Corporate Structure</option>
					<option value="Financial Information">Financial Information</option>
					<option value="Policy">Policy</option>
					<option value="Service">Service</option>
					<option value="Solicitation">Solicitation</option>
				</select></td>
			</tr>
			<tr>
				<td>Document Type:</td>
				<td><select name="doctype" style="width: 300px">
					<option></option>
					<option value="DOCTYPE1">DOCTYPE1</option>
					<option value="DOCTYPE2">DOCTYPE2</option>
					<option value="DOCTYPE3">DOCTYPE3</option>
					<option value="DOCTYPE4">DOCTYPE4</option>
					<option value="DOCTYPE5">DOCTYPE5</option>
				</select></td>
			</tr>
			<tr>
				<td>Effective Date(mm/dd/yyyy):</td>
				<td><input type="text" name="effectivedate" id="effectivedate" size="36"/></td>
			</tr>
			<tr>
				<td>Select the file to upload:</td>
				<td><input type="file" name="uploadfile" size="36" title="Browse..."></td>
			</tr>
			<tr>
				<td></td>
				<td><input type="button" name="back" title="Back" value="Back"/><input type="reset" name="reset" title="Cancel" value="Cancel"/>
				<input type="submit" name="Upload" title="Upload Document" value="Upload Document" /></td>
			</tr>
		</table>
		</form>
	</body>
</html>