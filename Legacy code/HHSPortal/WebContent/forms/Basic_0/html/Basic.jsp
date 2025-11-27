<%@ taglib prefix="fb" uri="/WEB-INF/tld/formbuilder-taglib.tld"%> 
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%><html lang="en"><portlet:defineObjects /><head><title>Untitled</title><meta http-equiv="Content-Type" content="text/html; charset=utf-8"></meta>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/forms/Basic_0/js/fb_Basic_1375178233979.js" charset="utf-8"></script>
<script type="text/javascript">

$(document)
.ready(
		function() {
			<%
			if(renderRequest.getAttribute("bappReadOnlyUser") !=null && ((String)renderRequest.getAttribute("bappReadOnlyUser")).equalsIgnoreCase("true")){%>
			$('#dropdownElt11337346065017dIi15v_select').prop("disabled",true);
			<%}%>
			
		});
var actionUrl = '<%=renderResponse.createActionURL()%>'+"&business_app_id="+'<%=renderRequest.getAttribute("business_app_id")%>'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>';
function submitForm(actionParameter) {
	var valDropDown = document.getElementById("dropdownElt11337346065017dIi15v_select").value;
	if(valDropDown.trim() == "" || valDropDown == null){
		$('#dropdownElt11337346065017dIi15v_select').parent().next("span").html("! This field is required.");
	}
	else{
	document.Basic.action = actionUrl + '&next_action=' + actionParameter;
	document.Basic.submit();
	}
}

// application status and org type fetched for Release 3.10.0 : Enhancement 6572
var status = '<%=renderRequest.getAttribute("appStatus")%>';
var orgType = '<%=renderRequest.getAttribute("orgType")%>';
var filingSubSectionFlag = '<%=renderRequest.getAttribute("filingSubSectionFlag")%>';
var corporateStructure = '<%=renderRequest.getAttribute("corporateStructure")%>';
var sectionStatus = '<%=renderRequest.getAttribute("sectionStatus")%>';

</script>
<style>
select{
width: 250px;
}
.accountingPeriod{
width:auto;
}
* {
	padding: 0px;
}
</style>
</head>
<body Style="padding:0mm 0mm; margin:0cm 0cm 0cm 0cm " bgcolor="#FFFFFF">
  <div id="fb_main" class="class_main formcontainer">
    <form action="<portlet:actionURL/>" method="post" name="Basic" id ="BasicsForm">
      <input type="hidden" name="fb_formName" value="Basic"></input><input type="hidden" name="fb_fileName" value="Basic"></input><input type="hidden" name="fb_formVersion" value="0"></input><input type="hidden" name="fb_language" value="English"></input><input type="hidden" name="fb_languageCode" value="en"></input><span id="property" password=" " username=" " driverclass=" " url=" " backgroundcolor="FFFFFF" filesize="2" fileunit="mb" makesqlfieldreadonly="false" convertdropdowntotextbox="false" defaultpermission="view" tablename="basicform" name="property" class="class_0">
      </span>
      <div id="fb_divBl0ck1" class="class_1"></div>
      <div id="fb_divBl0ck2" class="class_2">
        <fb:line id="lineElt31341744273703dIi15v" name="div_row_q" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="127" y1="4" x2="1670" x1="0">
          <div><h2 style="float:none">Basics</h2></div>
          <div style="min-height:3px; background-color:black; width:80%"></div>
          <div id="txtElt21337345932594dIi15v" lineid="lineElt31341744273703dIi15v" sectionname=" " y2="115" y1="36" x2="663" x1="7" tag="div" pos_rel="false" selectedindex="0" class="class_10">
            <span id="txtElt21337345932594dIi15v_textElement" class="class_12">
              <p>
                <span class="class_13">Please fill out the form below as accurately as possible. This information will determine what documents you are required to upload.</span>
              </p>
              <p>
                <span class="class_14"> </span>
              </p>
              <span class="class_15">
                <p class="class_16">
                  <span class="required">*</span>
                  <span class="class_18">Indicates required fields</span>
                </p>
              </span>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck3" class="class_19"></div>
      <div id="fb_divBl0ck4">
        <div id="errorEltDiv" y1="0" y2="0" x1="0" x2="0"></div>
      </div>
      <div id="fb_divBl0ck5" class="class_20"></div>
      <div id="fb_divBl0ck6" class="class_21">
        <fb:line id="lineElt21341744272223dIi15v" name="div_row_q0" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="158" y1="134" x2="1670" x1="0">
          <h3>General Information</h3>
        </fb:line>
      </div>
      <div id="fb_divBl0ck7" class="class_25"></div>
      <div id="fb_divBl0ck8" class="class_26">
        <fb:line id="lineElt11341740447664dIi15v" name="div_row_q1" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="215" y1="164" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt11337346065008dIi15v_label" title="Employer Identification Number/Tax Identification Number (EIN/TIN)" tag="label" for="textboxElt11337346065019dIi15v_textbox">Employer Identification Number/Tax Identification Number (EIN/TIN):</label>
            </span>
            <span class="formfield">
              <fb:input id="textboxElt11337346065019dIi15v_textbox" type="text" maxlength="10" size="30" defaultReadOnly="readonly" name="ein" cssclass=" input readonly class_29" value="" editrole=",edit" title="Enter the legal name of your organization as it appears on the Certificate of Incorporation." tabindex="-32768" viewrole=",read_only,semi_edit,admin,edit" errorcode=" Please enter alphanumeric value for Employer Identification Number/Tax Identification Number" schemaname="EIN_ID_NN" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11341740447664dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="EIN_ID_NN" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck9" class="class_32"></div>
      <div id="fb_divBl0ck10" class="class_33">
        <fb:line id="lineElt21341740478427dIi15v" name="div_row_q2" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="251" y1="216" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt1337346644242dIi15v_label" title="To update your Organization Legal Name, please go to the Organization Information screen." for="textboxElt21337346301590dIi15v_textbox">Organization Legal Name:</label>
            </span>
            <span class="formfield">
              <fb:input id="textboxElt21337346301590dIi15v_textbox" type="text" maxlength="100" size="30" defaultReadOnly="false" name="OLN" cssclass=" input class_37" value="" editrole=",admin,edit" title="To update your Organization Legal Name, please go to the Organization Information screen." tabindex="2" viewrole=",read_only,semi_edit" errorcode=" ! This field is required. ! This field is required." schemaname="ORGANIZATION_LEGAL_NAME" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21341740478427dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="ORGANIZATION_LEGAL_NAME" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck11" class="class_39"></div>
      <div id="fb_divBl0ck12" class="class_40">
        <fb:line id="lineElt31341740517770dIi15v" name="div_row_q3" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="285" y1="252" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt1337346644928dIi15v_label" title="Corporate Structure" tag="label" for="dropdownElt11337346065017dIi15v_select">Corporate Structure:</label>
            </span>
            <span class="formfield">
              <fb:select id="dropdownElt11337346065017dIi15v_select" name="CS" cssclass="class_44" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Corporate Structure" tabindex="5" viewrole=",read_only,semi_edit,admin,edit" errorcode=" ! This field is required." schemaname="CORPORATE_STRUCTURE_ID" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt31341740517770dIi15v">
                <option value=" " title=" " selected="selected">Select</option>
                <option value="Non Profit" title="Non Profit">Non Profit</option>
                <option value="For Profit" title="For Profit">For Profit</option>
              </fb:select>
            </span>
            <span class="formfield error">
              <fb:error id="CORPORATE_STRUCTURE_ID" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck13" class="class_46"></div>
      <div id="fb_divBl0ck14" class="class_47">
        <fb:line id="lineElt11341673038893dIi15v" name="div_row_q4" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="320" y1="286" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt1341669673678dIi15v_label" title="Entity Type" for="dropdownElt1341669712592dIi15v_select">Entity Type:</label>
            </span>
            <span class="formfield">
              <fb:select id="dropdownElt1341669712592dIi15v_select" name="entityType" cssclass="class_50" optionsize="6" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Entity Type" tabindex="6" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="ENTITY_TYPE_ID" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11341673038893dIi15v">
                <option value=" " title=" " selected="selected">Select</option>
                <option value="Corporation (any type)" title="Corporation (any type)">Corporation (any type)</option>
                <option value="LLC" title="LLC">LLC</option>
                <option value="Joint Venture" title="Joint Venture">Joint Venture</option>
                <option value="Partnership (any type)" title="Partnership (any type)">Partnership (any type)</option>
                <option value="Sole Proprietor" title="Sole Proprietor">Sole Proprietor</option>
                <option value="Other" title="Other">Other</option>
              </fb:select>
            </span>
            <span class="formfield error">
              <fb:error id="ENTITY_TYPE_ID" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck15" class="class_53"></div>
      <div id="fb_divBl0ck16" class="class_54">
        <fb:line id="lineElt11341673618816dIi15v" name="div_row_q5" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="428" y1="321" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required" tag="span">*</span>
            <label id="labelElt1341670185307dIi15v_label" title="Other, please specify" tag="label" for="textareaElt1341670227764dIi15v_textarea">Other, please specify:</label>
            </span>
            <span class="formfield" maxlength="150">
              <fb:textarea id="textareaElt1341670227764dIi15v_textarea" name="otherPleaseSpecify" cssclass=" input class_56" value="" editrole=",semi_edit,admin,edit" title="Other, Please Specify" tabindex="7" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="OTHER_PLEASE_SPECIFY" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11341673618816dIi15v" cols="32" rows="2"></fb:textarea>
            </span>
            <span class="formfield error">
              <fb:error id="OTHER_PLEASE_SPECIFY" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck17" class="class_60"></div>
      <div id="fb_divBl0ck18" class="class_61">
        <fb:line id="lineElt11341736542459dIi15v" name="div_row_q6" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="464" y1="429" x2="1670" x1="0">
          <div class= "row">
            <span class="label">
            <label> Dun and Bradstreet Number (DUNS#)</label>
            </span> 
            <span class="formfield">
              <fb:input id="textboxElt1337346326246dIi15v_textbox" type="text" maxlength="9" size="30" defaultReadOnly="false" name="DUNS" cssclass=" input class_65" value="" editrole=",semi_edit,admin,edit" title="A Data Universal Numbering System (D-U-N-S number) is a unique nine digit identification number which is linked to each specific physical location of a business. The number is assigned and maintained by Dun and Bradstreet (D&amp;B). " tabindex="8" viewrole=",read_only,semi_edit" errorcode=" ! This field is required. Please enter numeric only" schemaname="DUNS_ID" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt11341736542459dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="DUNS_ID" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck19" class="class_67"></div>
      <div id="fb_divBl0ck20" class="class_68"></div>
      <div id="fb_divBl0ck21" class="class_75"></div>
      <div id="fb_divBl0ck22" class="class_76">
        <fb:line id="lineElt141341740824669dIi15v" name="div_row_q8" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="585" y1="551" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt1337346648199dIi15v_label" title="To update your Accounting Period, please go to the Organization Information screen." tag="label" for="dropdownElt21337346321480dIi15v_select">Accounting Period From:</label>
            </span>
            <span class="formfield">
              <fb:select id="dropdownElt21337346321480dIi15v_select" name="Frommonth" cssclass="class_79 accountingPeriod" optionsize="13" firstfield="Select" defaultvalue=" " editrole=",admin,edit" title="Refers to the first month of the organization's fiscal year " tabindex="10" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="ACCOUNTING_PERIOD_START_MONTH" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt141341740824669dIi15v">
                <option value=" " title=" " selected="selected">Select</option>
                <option value="Jan" title="Jan">Jan</option>
                <option value="Feb" title="Feb">Feb</option>
                <option value="Mar" title="Mar">Mar</option>
                <option value="Apr" title="Apr">Apr</option>
                <option value="May" title="May">May</option>
                <option value="Jun" title="Jun">Jun</option>
                <option value="Jul" title="Jul">Jul</option>
                <option value="Aug" title="Aug">Aug</option>
                <option value="Sep" title="Sep">Sep</option>
                <option value="Oct" title="Oct">Oct</option>
                <option value="Nov" title="Nov">Nov</option>
                <option value="Dec" title="Dec">Dec</option>
              </fb:select>
              * To
              <fb:select id="dropdownElt1337348257397dIi15v_select" name="Tomonth" cssclass="class_82 accountingPeriod" optionsize="13" firstfield="Select" defaultvalue=" " editrole=",admin,edit" title="Refers to the last month of the organization's fiscal year " tabindex="11" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="ACCOUNTING_PERIOD_END_MONTH" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt141341740824669dIi15v">
                <option value=" " title=" " selected="selected">Select</option>
                <option value="Jan" title="Jan">Jan</option>
                <option value="Feb" title="Feb">Feb</option>
                <option value="Mar" title="Mar">Mar</option>
                <option value="Apr" title="Apr">Apr</option>
                <option value="May" title="May">May</option>
                <option value="Jun" title="Jun">Jun</option>
                <option value="Jul" title="Jul">Jul</option>
                <option value="Aug" title="Aug">Aug</option>
                <option value="Sep" title="Sep">Sep</option>
                <option value="Oct" title="Oct">Oct</option>
                <option value="Nov" title="Nov">Nov</option>
                <option value="Dec" title="Dec">Dec</option>
              </fb:select>
            </span>
            <span class="formfield error">
              <fb:error id="ACCOUNTING_PERIOD_START_MONTH" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck23" class="class_88"></div>
      <div id="fb_divBl0ck24" class="class_89">
        <fb:line id="lineElt151341740825621dIi15v" name="div_row_q9" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="692" y1="586" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt1337346698881dIi15v_label" title="Mission Statement" tag="label" for="textareaElt11337346065018dIi15v_textarea">Mission Statement:</label>
            </span>
            <span class="formfield" maxlength="1000">
              <fb:textarea id="textareaElt11337346065018dIi15v_textarea" name="Ministatement" cssclass=" input class_92" value="" editrole=",semi_edit,admin,edit" title="Mission Statement" tabindex="13" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="MISSION_STATEMENT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt151341740825621dIi15v" cols="32" rows="2"></fb:textarea>
            </span>
            <span class="formfield error">
              <fb:error id="MISSION_STATEMENT" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck25" class="class_95"></div>
      <div id="fb_divBl0ck26" class="class_96">
        <fb:line id="lineElt131341740819786dIi15v" name="div_row_q10" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="716" y1="693" x2="1670" x1="0">
          <h3>Executive Office Contact Information</h3>
        </fb:line>
      </div>
      <div id="fb_divBl0ck27" class="class_100"></div>
      <div id="fb_divBl0ck28" class="class_101">
        <fb:line id="lineElt91341740753829dIi15v" name="div_row_q11" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="752" y1="717" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt1337346700976dIi15v_label" title="Address Line 1" tag="label" for="textboxElt21337346309312dIi15v_textbox">Address Line 1:</label>
            </span>
            <span class="formfield">
              <fb:input id="textboxElt21337346309312dIi15v_textbox" type="text" maxlength="60" size="30" defaultReadOnly="false" name="Address1" cssclass=" input class_104" value="" editrole=",semi_edit,admin,edit" title="Address Line 1" tabindex="20" viewrole=",read_only,semi_edit" errorcode=" ! This field is required. Please enter alpha numeric" schemaname="ADDRESS_1" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt91341740753829dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="ADDRESS_1" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck29" class="class_107"></div>
      <div id="fb_divBl0ck30" class="class_108">
        <fb:line id="lineElt111341740774359dIi15v" name="div_row_q12" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="786" y1="753" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <label> Address Line 2:</label>
            </span>
            <span class="formfield">
              <fb:input id="textboxElt1337346384229dIi15v_textbox" type="text" maxlength="60" size="30" defaultReadOnly="false" name="Address2" cssclass=" input class_111" value="" editrole=",semi_edit,admin,edit" title="Address Line 2" tabindex="21" viewrole=",read_only,semi_edit" errorcode=" Please enter alpha numeric " schemaname="ADDRESS_2" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt111341740774359dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="ADDRESS_2" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck31" class="class_114"></div>
      <div id="fb_divBl0ck32" class="class_115">
        <fb:line id="lineElt101341740773548dIi15v" name="div_row_q13" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="821" y1="787" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required" tag="span">*</span>
            <label id="labelElt1337346701816dIi15v_label" title="City" tag="label" for="textboxElt1337346389580dIi15v_textbox">City:</label>
            </span> 
            <span class="formfield">
              <fb:input id="textboxElt1337346389580dIi15v_textbox" type="text" maxlength="40" size="30" defaultReadOnly="false" name="city" cssclass=" input class_118" value="" editrole=",semi_edit,admin,edit" title="City" tabindex="22" viewrole=",read_only,semi_edit" errorcode=" ! This field is required. Please enter string only" schemaname="CITY" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt101341740773548dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="CITY" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck33" class="class_121"></div>
      <div id="fb_divBl0ck34" class="class_122">
        <fb:line id="lineElt81341740728401dIi15v" name="div_row_q14" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="855" y1="822" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required" tag="span">*</span>
            <label id="labelElt1337346702449dIi15v_label" title="State" tag="label" for="dropdownElt1337346399794dIi15v_select">State:</label>
            </span> 
            <span class="formfield">
              <fb:select id="dropdownElt1337346399794dIi15v_select" name="state" cssclass="class_126" optionsize="57" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="State" tabindex="23" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="STATE" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt81341740728401dIi15v">
                <option value=" " title=" " selected="selected">Select</option>
                <option value="AK" title="AK">AK</option>
                <option value="AL" title="AL">AL</option>
                <option value="AR" title="AR">AR</option>
                <option value="AS" title="AS">AS</option>
                <option value="AZ" title="AZ">AZ</option>
                <option value="CA" title="CA">CA</option>
                <option value="CO" title="CO">CO</option>
                <option value="CT" title="CT">CT</option>
                <option value="DC" title="DC">DC</option>
                <option value="DE" title="DE">DE</option>
                <option value="FL" title="FL">FL</option>
                <option value="GA" title="GA">GA</option>
                <option value="GU" title="GU">GU</option>
                <option value="HI" title="HI">HI</option>
                <option value="IA" title="IA">IA</option>
                <option value="ID" title="ID">ID</option>
                <option value="IL" title="IL">IL</option>
                <option value="IN" title="IN">IN</option>
                <option value="KS" title="KS">KS</option>
                <option value="KY" title="KY">KY</option>
                <option value="LA" title="LA">LA</option>
                <option value="MA" title="MA">MA</option>
                <option value="MD" title="MD">MD</option>
                <option value="ME" title="ME">ME</option>
                <option value="MI" title="MI">MI</option>
                <option value="MN" title="MN">MN</option>
                <option value="MO" title="MO">MO</option>
                <option value="MP" title="MP">MP</option>
                <option value="MS" title="MS">MS</option>
                <option value="MT" title="MT">MT</option>
                <option value="NC" title="NC">NC</option>
                <option value="ND" title="ND">ND</option>
                <option value="NE" title="NE">NE</option>
                <option value="NH" title="NH">NH</option>
                <option value="NJ" title="NJ">NJ</option>
                <option value="NM" title="NM">NM</option>
                <option value="NV" title="NV">NV</option>
                <option value="NY" title="NY">NY</option>
                <option value="OH" title="OH">OH</option>
                <option value="OK" title="OK">OK</option>
                <option value="OR" title="OR">OR</option>
                <option value="PA" title="PA">PA</option>
                <option value="PR" title="PR">PR</option>
                <option value="RI" title="RI">RI</option>
                <option value="SC" title="SC">SC</option>
                <option value="SD" title="SD">SD</option>
                <option value="TN" title="TN">TN</option>
                <option value="TX" title="TX">TX</option>
                <option value="UT" title="UT">UT</option>
                <option value="VA" title="VA">VA</option>
                <option value="VI" title="VI">VI</option>
                <option value="VT" title="VT">VT</option>
                <option value="WA" title="WA">WA</option>
                <option value="WI" title="WI">WI</option>
                <option value="WV" title="WV">WV</option>
                <option value="WY" title="WY">WY</option>
              </fb:select>
            </span>
            <span class="formfield error">
              <fb:error id="STATE" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck35" class="class_128"></div>
      <div id="fb_divBl0ck36" class="class_129">
        <fb:line id="lineElt71341740670884dIi15v" name="div_row_q15" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="890" y1="856" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required" tag="span">*</span>
            <label id="labelElt1337346702747dIi15v_label" title="Zip Code" tag="label" for="textboxElt1337346406216dIi15v_textbox" class="class_131">Zip Code:</label>
            </span>
            <span class="formfield">
              <fb:input id="textboxElt1337346406216dIi15v_textbox" type="text" maxlength="5" size="30" defaultReadOnly="false" name="Zipcode" cssclass=" input class_133" value="" editrole=",semi_edit,admin,edit" title="Zipcode" tabindex="24" viewrole=",read_only,semi_edit" errorcode=" ! This field is required. Please enter number only" schemaname="ZIP_CODE" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt71341740670884dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="ZIP_CODE" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck37" class="class_136"></div>
      <div id="fb_divBl0ck38" class="class_137">
        <fb:line id="lineElt61341740639668dIi15v" name="div_row_q16" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="923" y1="891" x2="1670" x1="0">
          <div class="row">
            <span class= "label"> 
            <span class="required" tag="span">*</span>
            <label id="labelElt1337346703085dIi15v_label" title="Phone Number" tag="label" for="textboxElt1337346411639dIi15v_textbox">Phone Number:</label>
            </span> 
            <span class= "formfield">
              <fb:input id="textboxElt1337346411639dIi15v_textbox" type="text" maxlength="12" size="30" defaultReadOnly="false" name="phoneno" cssclass=" input class_140" value="" editrole=",semi_edit,admin,edit" title="Phone Number" tabindex="25" viewrole=",read_only,semi_edit" errorcode=" ! This field is required. Please enter numeric only" schemaname="PHONE_NUMBER" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt61341740639668dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="PHONE_NUMBER" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck39" class="class_143"></div>
      <div id="fb_divBl0ck40" class="class_144">
        <fb:line id="lineElt51341740599420dIi15v" name="div_row_q17" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="956" y1="924" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <label> Website </label>
            </span>
            <span class="formfield">
              <fb:input id="textboxElt1337346418718dIi15v_textbox" type="text" maxlength="90" size="30" defaultReadOnly="false" name="websiteaddress" cssclass=" input class_147" value="" editrole=",semi_edit,admin,edit" title="Website" tabindex="26" viewrole=",read_only,semi_edit" errorcode=" Please enter alpha numeric only" schemaname="WEBSITE" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt51341740599420dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="WEBSITE" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck41" class="class_150"></div>
      <div id="fb_divBl0ck42" class="class_151">
        <fb:line id="lineElt41341740561434dIi15v" name="div_row_q18" visibilityvalue="visibilityvalue" condition="condition" lineid="undefined" sectionname=" " y2="1066" y1="957" x2="1670" x1="0">
          <div class="row">
            <span class="label"><span class="required">*</span>
            <label id="labelElt1338368457812dIi15v_label" title="Indicate types of Social Media" tag="label" for="checkboxElt11337346438773dIi15v_checkbox">Indicate types of Social Media:</label></span>
            <span class="formfield">
              <div>
                <fb:input id="checkboxElt11337346438773dIi15v_checkbox" type="checkbox" name="twittersm" value="twittersmvalue" editrole=",semi_edit,admin,edit" title="Twitter" tabindex="30" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="TWITTER_SM" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt41341740561434dIi15v"></fb:input>
                <label id="txtElt11337349682924dIi15v_textElement" class="class_155">
                Twitter
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt1337346486175dIi15v_checkbox" type="checkbox" name="facebooksm" value="facebooksmvalue" editrole=",semi_edit,admin,edit" title="Facebook Page" tabindex="31" viewrole=",read_only,semi_edit" errorcode=" value" schemaname="FACEBOOK_SM" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt41341740561434dIi15v"></fb:input>
                <label id="txtElt1337349758184dIi15v_textElement" class="class_161">
                Facebook Page
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt1337346487553dIi15v_checkbox" type="checkbox" name="othersm" value="othersmvalue" editrole=",semi_edit,admin,edit" title="Other" tabindex="32" viewrole=",read_only,semi_edit" errorcode=" value" schemaname="OTHER_SM" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt41341740561434dIi15v"></fb:input>
                <label id="txtElt1337349759126dIi15v_textElement" class="class_168">
                Other
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt1337346494035dIi15v_checkbox" type="checkbox" name="nonesm" value="nonesmvalue" editrole=",semi_edit,admin,edit" title="None of the above" tabindex="33" viewrole=",read_only,semi_edit" errorcode=" value" schemaname="NONE_SM" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt41341740561434dIi15v"></fb:input>
                <label id="txtElt1337349759912dIi15v_textElement" class="class_174">
                None of the above
                </label>
              </div>
            </span>
            <span class="formfield error">
              <fb:error id="TWITTER_SM" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck43" class="class_178"></div>
      <div id="fb_divBl0ck44" class="class_179">
        <fb:line id="lineElt21341673049517dIi15v" name="div_row_q19" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1100" y1="1067" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required" tag="span">*</span>
            <label id="labelElt1337346728800dIi15v_label" title="Twitter @" tag="label" for="textboxElt1337346501774dIi15v_textbox">Twitter @:</label>
            </span>
            <span class="formfield">
              <fb:input id="textboxElt1337346501774dIi15v_textbox" type="text" maxlength="15" size="30" defaultReadOnly="false" name="twitter" cssclass=" input class_183" value="" editrole=",semi_edit,admin,edit" title="Twitter" tabindex="34" viewrole=",read_only,semi_edit" errorcode=" ! This field is required. Please enter alpha numeric only" schemaname="TWITTER" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21341673049517dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="TWITTER" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck45" class="class_185"></div>
      <div id="fb_divBl0ck46" class="class_186">
        <fb:line id="lineElt31341673207514dIi15v" name="div_row_q20" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1134" y1="1101" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt1337349703862dIi15v_label" title="Facebook Page" tag="label" for="textboxElt1337346504433dIi15v_textbox">Facebook Page:</label>
            </span> 
            <span class="formfield">
              <fb:input id="textboxElt1337346504433dIi15v_textbox" type="text" maxlength="150" size="30" defaultReadOnly="false" name="facebookpage" cssclass=" input class_190" value="" editrole=",semi_edit,admin,edit" title="Facebook Page" tabindex="35" viewrole=",read_only,semi_edit" errorcode=" ! This field is required. Please enter alpha numeric only" schemaname="FACEBOOK_PAGE" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt31341673207514dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="FACEBOOK_PAGE" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck47" class="class_192"></div>
      <div id="fb_divBl0ck48" class="class_193">
        <fb:line id="lineElt41341673332012dIi15v" name="div_row_q21" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1170" y1="1135" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required" tag="span">*</span>
            <label id="labelElt1341673421360dIi15v_label" title="If Other, please specify" tag="label" for="textboxElt1341673421359dIi15v_textbox">If Other, please specify:</label>
            </span>
            <span class="formfield">
              <fb:input id="textboxElt1341673421359dIi15v_textbox" type="text" maxlength="150" size="30" defaultReadOnly="false" name="ifOther" cssclass=" input class_197" value="" editrole=",semi_edit,admin,edit" title="If Other, please specify" tabindex="36" viewrole=",read_only,semi_edit" errorcode=" ! This field is required. Please enter alpha numeric only" schemaname="IF_OTHER_SOCIAL" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt41341673332012dIi15v"></fb:input>
            </span>
            <span class="formfield error">
              <fb:error id="IF_OTHER_SOCIAL" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck49" class="class_199"></div>
      <div id="fb_divBl0ck50" class="class_200">
        <fb:line id="lineElt11341744266342dIi15v" name="div_row_q22" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="1223" y1="1172" x2="1670" x1="0">
          <div class="buttonholder">
            <fb:button id="buttonElt11341668163225dIi15v_button" type="button" name="save" cssclass=" button class_202" value=" Save " editrole=",semi_edit,admin,edit" title="Save" events="eventname" tabindex="37" viewrole=",read_only,semi_edit" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" lineid="lineElt11341744266342dIi15v" defaultpermission="view"></fb:button>
            <fb:button id="buttonElt11337919586178dIi15v_button" type="button" name="save_next" cssclass=" button class_205" value=" Save &amp; Next " editrole=",semi_edit,admin,edit" title="Save &amp; Next" events="eventname" tabindex="38" viewrole=",read_only,semi_edit" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" lineid="lineElt11341744266342dIi15v" defaultpermission="view"></fb:button>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck51" class="class_207"></div>
      <fb:input id="fbjscounter" type="hidden" name="fbjscounter" value="1"></fb:input>
    </form>
  </div>
  <jsp:include page="/WEB-INF/jsp/businessapplication/basicOverlays.jsp"></jsp:include>
</body>
</html>