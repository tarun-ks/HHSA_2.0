<!--
    The footer skeleton file renders a HTML DIV element for the footer.  The DIV element contains all children
    of the footer including layouts, content controls, portlets, etc.
-->
<jsp:root version="2.0" 
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton"
>
    <jsp:directive.page session="false" />
    <jsp:directive.page isELIgnored="false" />
    <skeleton:context type="footerpc">
        <skeleton:control name="div" presentationContext="${footerpc}" presentationClass="wlp-bighorn-footer">
            <skeleton:children />
        </skeleton:control>
    </skeleton:context>
</jsp:root>
