<!--
    The window skeleton file renders a HTML DIV element for the window.  This DIV element contains a titlebar
    and window content.  The window content is contained within an additional HTML DIV element.  The window content
    and its containing DIV is rendered only if the window is not minimized.
-->
<jsp:root version="2.0" 
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton"
>
    <jsp:directive.page session="false" />
    <jsp:directive.page isELIgnored="false" />
    <jsp:directive.page import="com.bea.netuix.servlets.controls.window.TitlebarPresentationContext" />
    <jsp:directive.page import="com.bea.netuix.servlets.controls.window.WindowPresentationContext" />
    <skeleton:context type="windowpc">
        <c:if test="${windowpc.packed}">
            <c:set var="class1">wlp-bighorn-window-packed</c:set>        
        </c:if>
        <c:if test="${windowpc.contentOnly}">
            <c:set var="class2">wlp-bighorn-window-content-only</c:set>
        </c:if>
        <skeleton:control name="div" presentationContext="${windowpc}" 
            presentationClass="wlp-bighorn-window ${class1} ${class2}" presentationId="${windowpc.label}"
        >
            <jsp:scriptlet>
                TitlebarPresentationContext tpc = ((WindowPresentationContext)windowpc).getTitlebarPresentationContext();
                if (tpc != null) {
                    tpc.setVisible(false);
                }
            </jsp:scriptlet>
            <c:if test="${! (windowpc.windowState.name == 'minimized')}">
                <skeleton:control name="div" content="true" presentationContext="${windowpc}"
                    presentationClass="wlp-bighorn-window-content"
                >
                    <skeleton:children/>
                </skeleton:control>
            </c:if>
        </skeleton:control>
    </skeleton:context>
</jsp:root>
