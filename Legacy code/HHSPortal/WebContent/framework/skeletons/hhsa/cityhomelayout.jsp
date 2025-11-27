<jsp:root version="2.0" 
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton">
<jsp:directive.page session="false" />
<jsp:directive.page isELIgnored="false" />

<skeleton:context type="layoutpc">
<skeleton:control name="table" presentationContext="${layoutpc}" style='width:100%;'  >
<c:set var="ph" value="${layoutpc.placeholders}"/>
<c:set var="first"  value="${ph[0]}"/>
<c:set var="second" value="${ph[1]}"/>
<c:set var="third"  value="${ph[2]}"/>
<c:set var="fourth" value="${ph[3]}"/>
<c:set var="five"   value="${ph[4]}"/>
<c:set var="six" value="${ph[5]}"/>
<c:set var="seven"  value="${ph[6]}"/>
<tr>
	<c:if test="${org_type eq 'city_org'}">
		<td>
		 <h2>HHS Accelerator Homepage</h2>
		</td>
				</c:if>
	<c:if test="${org_type eq 'agency_org'}">
		<td>
			<jsp:include page="/framework/skeletons/hhsa/homeagencytitle.jsp"></jsp:include>
		</td>
		<td>
		<div id="helpIcon" class="iconQuestion marginReset"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
		<input type="hidden" id="screenName" value="Home (Agency)" name="screenName"/>
		</td>
		<div class="overlay"></div>
		<div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
		</div>
	</c:if>
</tr>
<tr>
    <td  valign="top" width="100%" colspan='2'>
    	<table width="100%" height="100%">
    		<tr>
	    		<td  valign="top" width="${first.width}" style="padding-right:5px;">
			      <skeleton:child presentationContext="${first}"/>
			    </td>
			    <td  valign="top"  width="${second.width}" style="padding-right:5px;">
			      <skeleton:child presentationContext="${second}"/>
			    </td>
			    <td  valign="top" width="${third.width}">
			      <skeleton:child presentationContext="${third}"/>
			    </td>
			</tr>
    	</table>
    </td>
  </tr>
  <tr><td>&nbsp;</td></tr>
  <tr>
    <td  valign="top" width="100%"  colspan='2'>
     	<table  width="100%" height="100%">
    		<tr>
	    		<td  valign="top" width="${fourth.width}" style="padding-right:5px;">
			     <skeleton:child presentationContext="${fourth}"/>
			    </td>
			    <td  valign="top" width="${five.width}">
			      <skeleton:child presentationContext="${five}"/>
			    </td>
			</tr>
    	</table>
    </td>
  </tr>
  <tr><td>&nbsp;</td></tr>
  <tr>
    <td  valign="top" width="100%"  colspan='2'>
     	<table  width="100%" height="100%">
    		<tr>
	    		<td  valign="top" width="${six.width}" style="padding-right:5px;">
			     <skeleton:child presentationContext="${six}"/>
			    </td>
			    <td  valign="top" width="${seven.width}">
			      <skeleton:child presentationContext="${seven}"/>
			    </td>
			</tr>
    	</table>
    </td>
  </tr>
</skeleton:control>
</skeleton:context>
</jsp:root>