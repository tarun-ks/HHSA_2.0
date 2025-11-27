<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<option id="-1" value=""></option>
<c:forEach var="programNameVar" items="${programNameList}">
	<option value="${programNameVar.programId}" title="${programNameVar.programName}">${programNameVar.programName}</option>			
</c:forEach>			