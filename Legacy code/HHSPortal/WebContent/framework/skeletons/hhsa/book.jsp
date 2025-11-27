<!--
    The book skeleton file renders a HTML DIV element for the book.  This DIV element contains a menu and
    book content.  The book content is contained within an additional HTML DIV element.
-->
<jsp:root version="2.0" 
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton"
>
    <jsp:directive.page session="false" />
    <jsp:directive.page isELIgnored="false" />
    <skeleton:context type="bookpc">
    <c:set var="classname" value=""></c:set>
    <c:if test="${bookpc.label == 'portlet_hhsweb_portal_book_1'}">
    	<c:set var="classname" value="bodycontainer"></c:set>
    </c:if>
        <skeleton:control name="div" presentationContext="${bookpc}"
            presentationId="${bookpc.label}"
        ><!-- presentationClass="wlp-bighorn-book"  -->
            <skeleton:child presentationContext="${bookpc.titlebarPresentationContext}"/>
            <skeleton:child presentationContext="${bookpc.menuPresentationContext}"/>
            <skeleton:control name="div"  presentationContext="${bookpc}"
                presentationClass="${classname}" presentationId="mymain"
            ><!-- wlp-bighorn-book-content  -->
            
         
                <skeleton:children/>
            </skeleton:control>
        </skeleton:control>
        
    </skeleton:context>
</jsp:root>
