<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<!-- Overlay for document linkages-->
<div class="alert-box alert-box-linkDocStatus" style="display: none">
	<div class="content">
		<div id="newTabs">
			<div class="tabularCustomHead" >Document Linkages</div>

			<div class='overlayWrapper formcontainer' style="padding-right:12px;">
				<div class="messagedivover" id="messagedivover"></div>
				</br>
				<b
					style="font-family: 'Verdana Bold', 'Verdana'; font-weight: 700; font-style: normal; font-size: 20px; color: #1969B0;border-bottom: dotted 1px;">Document
					Linkages</b>
					</br></br>

				<div class="" >
				<st:table objectName="fetchedData" cssClass="heading"
					alternateCss1="evenRows" alternateCss2="oddRows">
					<st:property headingName="Date" columnName="date"
						align="center" size="5%">
						<st:extension
							decoratorClass="com.nyc.hhs.frameworks.grid.LinkageGrid" />
					</st:property>
					<st:property headingName="Entity" columnName="entityLinked"
						align="center" sortType="dateLinked" sortValue="asc" size="30%">
						<st:extension
							decoratorClass="com.nyc.hhs.frameworks.grid.LinkageGrid" />
					</st:property>
				</st:table>
				</div>

				
			</div>
			<div class="buttonholder" style="padding-right:12px;">
				<input type="button" id="linkedCancelButton"  name="cancelButton"
					title="Cancel" value="Cancel"  />

			</div>
		</div>
	</div>
	<a href="javascript:void(0);" class="exit-panel upload-exit"
		title="Exit">&nbsp;</a>
</div>
