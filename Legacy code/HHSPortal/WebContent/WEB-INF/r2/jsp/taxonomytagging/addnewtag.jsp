<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="maxCount" value="0"/>
<c:forEach items="${taxonomyTaggingList}" var="item" varStatus="theCount">
	<tr id="current${theCount.index}">
		<c:set var="maxCount" value="${theCount.index}"/>
		<td class='servicef'>${item.serviceFunctionName}</td>
		<c:set var="modifierList" value=""/>
		<c:forEach items="${item.taggedElementName}" var="item1" varStatus="loop">
			<c:set var="modifierList">${modifierList}${item1}<c:if test="${!loop.last}">, </c:if></c:set>	
		</c:forEach>
		<td class='modifers'>${modifierList}</td>
		<td>
			<select onchange="onchangeBulk(this, '${theCount.index}')">
				<option value=I need to... >I need to...</option>
				<option>Edit Tag</option>
				<option>Delete Tag</option>
			</select>
			<input type = "hidden" id="serviceId" name="serviceId" value="${item.elementId}"/>
			<input type = "hidden" id="modifierIds" name="modifierIds" value="${item.modifiers}"/>
			<c:set var="completePath" value="${fn:replace(item.completeBranchPath, '>k3yv@lu3S3p@r@t0r', 'k3yv@lu3S3p@r@t0r')}"/>
			<input type = "hidden" id="modifierListComplete" name="modifierListComplete" value="${completePath}"/>
			<input type = "hidden" id="taxonomyTaggingId" name="taxonomyTaggingId" value="${item.taxonomyTaggingId}"/>
		</td>
	</tr>
</c:forEach>
<script>
	idsGenerated = ${maxCount};
</script>