<!--
    The desktop skeleton file renders the HTML DOCTYPE declaration for the desktop.
-->
<jsp:root version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton"
>
			<c:choose>
				<c:when test="${param.app_menu_name ne null }">
					<c:set var="app_menu_name" value="${param.app_menu_name}" scope="session"></c:set>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${app_menu_name ne null or session.app_menu_name ne null }">
							<c:set var="app_menu_name" value="${app_menu_name}" scope="session"></c:set>
						</c:when>
						<c:otherwise>
							<c:set var="app_menu_name" value="home_icon" scope="session"></c:set>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
    <jsp:directive.page session="false"/>
    <jsp:directive.page isELIgnored="false"/>
    <skeleton:context type="desktoppc">
        <skeleton:doctype/>
    </skeleton:context>
</jsp:root>
