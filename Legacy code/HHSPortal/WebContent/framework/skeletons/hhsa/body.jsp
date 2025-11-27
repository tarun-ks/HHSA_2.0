<!--
    The body skeleton file renders the HTML BODY element for the body control.  This BODY element contains
    the visible Portal control elements.
-->
<jsp:root version="2.0" 
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton"
>
    <jsp:directive.page session="false" />
    <jsp:directive.page isELIgnored="false" />
    <skeleton:context type="bodypc">
        <skeleton:control name="body" presentationContext="${bodypc}">
        <div id="main-wrapper">
            <skeleton:children/>
        </div>
        </skeleton:control>
    </skeleton:context>
</jsp:root>
