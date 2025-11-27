<!--
    The header skeleton file renders a HTML DIV element for the header.  The DIV element contains all children
    of the header including layouts, content controls, portlets, etc.
-->
<jsp:root version="2.0" 
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton"
>
    <jsp:directive.page session="false" />
    <jsp:directive.page isELIgnored="false" />
    <skeleton:context type="headerpc">
        <skeleton:control name="div" presentationContext="${headerpc}" presentationClass="wlp-bighorn-header">
            <skeleton:children />
        </skeleton:control>
    </skeleton:context>
</jsp:root>
