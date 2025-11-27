
							<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
							<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
							<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
							<portlet:defineObjects/>
							<c:set value='<%=portletSession.getAttribute("programNameList")%>' var="programNameList" ></c:set>
							
<option id="-1" value=""></option>
<c:forEach var="programNameVar" items="${programNameList}">
	<option value="${programNameVar.programId}" title="${programNameVar.programName}">${programNameVar.programName}</option>			
</c:forEach>			

