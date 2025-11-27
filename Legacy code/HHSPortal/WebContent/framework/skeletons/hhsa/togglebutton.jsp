<jsp:root version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:render="http://www.bea.com/servers/portal/tags/netuix/render"
>
    <jsp:directive.page session="false"/>
    <jsp:directive.page isELIgnored="false"/>
    <skeleton:context type="abstractbuttonpc">

        <!-- Bug 8702966: do not pass the button URL as a param to this included JSP page.
                          A multi-byte char in the path of the URL may be corrupted. -->
        <render:toggleButtonUrl var="buttonUrl"/>

        <!-- Note that "href", etc. are dynamic attributes and passed directly to the HTML output. -->
        <skeleton:control name="a" presentationContext="${abstractbuttonpc}" href="${pageScope.buttonUrl}">
            <c:if test="${! empty abstractbuttonpc.rolloverImage}">
                <c:set var="imageclass" value="wlp-bighorn-image-nonrollover"/>
                <img src="${abstractbuttonpc.rolloverImageSrc}" alt="${abstractbuttonpc.altText}" title="${abstractbuttonpc.altText}" name="${abstractbuttonpc.name}" class="wlp-bighorn-image-rollover"/>
            </c:if>
            <img src="${abstractbuttonpc.imageSrc}" alt="${abstractbuttonpc.altText}" title="${abstractbuttonpc.altText}" name="${abstractbuttonpc.name}" class="${imageclass}"/>
            <c:remove var="imageclass"/>
        </skeleton:control>

    </skeleton:context>
</jsp:root>
