<jsp:root version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:render="http://www.bea.com/servers/portal/tags/netuix/render"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton"
>
    <jsp:directive.page session="false"/>
    <jsp:directive.page isELIgnored="false"/>
    <skeleton:context type="buttonpc">
        <!-- Delete buttons are only applicable to portlets -->
        <c:if test="${buttonpc.parentPortlet}">

			<!-- Bug 8702966: do not pass the button URL as a param to this included JSP page.
                          A multi-byte char in the path of the URL may be corrupted. -->
			<render:toggleButtonUrl var="buttonUrl"/>

			<!-- Note that "href", etc. are dynamic attributes and passed directly to the HTML output. -->
			<skeleton:control name="a" presentationContext="${buttonpc}" href="${pageScope.buttonUrl}" onclick="return wlp_bighorn_delete_handler(this)">
				<c:if test="${! empty buttonpc.rolloverImage}">
					<c:set var="imageclass" value="wlp-bighorn-image-nonrollover"/>
					<img src="${buttonpc.rolloverImageSrc}" alt="${buttonpc.altText}" title="${buttonpc.altText}" name="${buttonpc.name}" class="wlp-bighorn-image-rollover"/>
				</c:if>
				<img src="${buttonpc.imageSrc}" alt="${buttonpc.altText}" title="${buttonpc.altText}" name="${buttonpc.name}" class="${imageclass}"/>
				<c:remove var="imageclass"/>
			</skeleton:control>
        </c:if>
    </skeleton:context>
</jsp:root>
