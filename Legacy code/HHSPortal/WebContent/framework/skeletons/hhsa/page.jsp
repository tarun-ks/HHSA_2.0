<!--
    The page skeleton file renders a HTML DIV element for the page.  The DIV element contains all the children
    of the page including layouts, portlets, etc.
-->
<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton">
<jsp:directive.page language="java" contentType="text/html;charset=UTF-8" /> 
<jsp:directive.page  import="com.bea.portlet.PageURL"/>
<jsp:directive.page import="com.bea.netuix.servlets.controls.page.PagePresentationContext"/>
<jsp:directive.page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"/>
<jsp:directive.page import="java.util.ArrayList"/>
<jsp:directive.page import="com.bea.netuix.servlets.controls.page.BookPresentationContext" />
<jsp:directive.page session="false" />
<jsp:directive.page isELIgnored="false" />
	<skeleton:context type="pagepc">
		<skeleton:control name="div" presentationContext="${pagepc}"
			presentationClass="container" presentationId="${pagepc.label}">
			<skeleton:children />
		</skeleton:control>
	</skeleton:context>
</jsp:root>
