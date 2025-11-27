<!--
    The singlelevelmenu skeleton file renders an HTML TABLE element for the menu. This TABLE contains two
    TD cells in a single TR row corresponding to the menu items and menu buttons. Menu item rendering is
    accomplished by delegating to a common helper file.
-->
<jsp:root version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton"
>
    <jsp:directive.page session="false" />
    <jsp:directive.page isELIgnored="false" />
    <skeleton:context type="menupc">
        <skeleton:control name="table" presentationContext="${menupc}"
            presentationClass="wlp-bighorn-menu wlp-bighorn-menu-single"
        >
            <tr>
                <td class="wlp-bighorn-menu-menu-panel">
                    <c:if test="${menupc.bookPresentationContext.windowMode.name == 'view'}">
                        <c:set var="bookpc" value="${menupc.bookPresentationContext}" scope="request"/>
                        <!--
                            This and other menu skeleton files delegate to a common helper file.
                        -->
                        <c:remove var="bookpc" scope="request"/>
                    </c:if>
                </td>
                <td class="wlp-bighorn-menu-button-panel">
                    <skeleton:children/>
                </td>
            </tr>
        </skeleton:control>
    </skeleton:context>
</jsp:root>
