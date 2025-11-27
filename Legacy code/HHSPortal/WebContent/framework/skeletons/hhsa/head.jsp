<!--
    The head skeleton file renders the HTML HEAD element for the head control.  This HEAD element contains
    a HTML META Content-Type directive as well as implicit elements created by the Portal Framework (e.g. style
    and script elements defined in skin.xml).
-->
<jsp:root version="2.0" 
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton"
>
    <jsp:directive.page session="false" />
    <jsp:directive.page isELIgnored="false" />
    <skeleton:context type="headpc">
        <head>
			<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7; IE=EDGE" />
			<meta name="robots" content="noindex, nofollow" />
            <skeleton:contentTypeMeta/>
            <skeleton:children/>
        </head>
    </skeleton:context>
</jsp:root>
