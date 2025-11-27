<jsp:root version="2.0" 
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton">
    
<jsp:directive.page session="false" />
<jsp:directive.page isELIgnored="false" />

<skeleton:context type="layoutpc">
<skeleton:control name="table" presentationContext="${layoutpc}" presentationStyle="width:100%"  >
<c:set var="ph" value="${layoutpc.placeholders}"/>
<c:set var="first"  value="${ph[0]}"/>
<c:set var="second" value="${ph[1]}"/>

<tr>
	<td>
		<jsp:include page="/framework/skeletons/hhsa/homeagencytitle.jsp"></jsp:include>
	</td>
	<td>
		<div id="helpIcon" class="iconQuestion marginReset"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
		<input type="hidden" id="screenName" value="Home (Agency)" name="screenName"/>
	</td>
</tr>
<tr>
	<td valign="top" width="${first.width}"  style="padding-right:10px;">
	    <skeleton:child presentationContext="${first}"/>
	 </td>
	 <td width="${second.width}" valign="top">
	    <skeleton:child presentationContext="${second}"/>
	 </td>
</tr>

</skeleton:control>
</skeleton:context>
</jsp:root>