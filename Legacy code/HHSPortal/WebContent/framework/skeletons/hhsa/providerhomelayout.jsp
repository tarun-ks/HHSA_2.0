<jsp:root version="2.0" xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:skeleton="http://www.bea.com/servers/portal/tags/netuix/skeleton">

	<jsp:directive.page session="false" />
	<jsp:directive.page isELIgnored="false" />
    <!-- Start : R5 Updated-->
	<skeleton:context type="layoutpc">
		<skeleton:control name="table" presentationContext="${layoutpc}"
			presentationStyle="width:100%">
			<c:set var="ph" value="${layoutpc.placeholders}" />
			<c:set var="first" value="${ph[0]}" />
 			<c:set var="second" value="${ph[1]}" />
			<c:set var="third" value="${ph[2]}" />
			<c:set var="fourth" value="${ph[3]}" />
			<c:set var="five" value="${ph[4]}" />
			<tr>
				<td>
					<h2>Provider Homepage</h2>
				</td>
				<td>
					<div id="helpIcon" class="iconQuestion marginReset">
						<a href="javascript:void(0);" title="Need Help?"
							onclick="smFinancePageSpecificHelp();"></a>
					</div> <input type="hidden" id="screenName" value="Home (Provider)"
					name="screenName" />
				</td>
				<div class="overlay"></div>
				<div class="alert-box-help">
					<div class="tabularCustomHead toplevelheaderHelp"></div>
					<div id="helpPageDiv"></div>
					<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
				</div>
				<div class="alert-box-contact">
					<div class="content">
						<div id="newTabs">
							<div id="contactDiv"></div>
						</div>
					</div>
				</div>
			</tr>
			<c:if test="${param.alertPermissionError}">
				<tr>
					<td colspan="2" style="padding-bottom: 10px;">
						<div class='infoMessage' style="display: block">You do not
							have the necessary permissions to view the alert. Please contact
							your organization Administrator to request additional
							permissions.</div>
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="2" valign="top" width="${first.width}" >
					<skeleton:child presentationContext="${first}" />
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2" valign="top" width="${second.width}"><skeleton:child
						presentationContext="${second}" /></td>
			</tr>
		
			<tr>
				<td colspan="2" valign="top" width="${third.width}"><skeleton:child
						presentationContext="${third}" /></td>
			</tr>
			<tr>
				<td valign="top" width="${fourth.width}" style="padding-right: 10px;">
					<skeleton:child presentationContext="${fourth}" />
				</td>
				<td valign="top" width="${five.width}"><skeleton:child
						presentationContext="${five}" /></td>
			</tr>
		</skeleton:control>
	</skeleton:context>
    <!-- End : R5 Updated-->
</jsp:root>