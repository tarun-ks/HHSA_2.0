<%@ taglib prefix="fb" uri="/WEB-INF/tld/formbuilder-taglib.tld"%><%@ page errorPage="/error/errorpage.jsp" %> <%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%><html lang="en"><portlet:defineObjects /><head><title>Untitled</title><meta http-equiv="Content-Type" content="text/html; charset=utf-8"></meta>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/forms/Board_0/js/fb_Board_1358244552769.js" charset="utf-8"></script>
	<script type="text/javascript">
		var actionUrl  = '<%=renderResponse.createActionURL()%>'+"&business_app_id="+'<%=renderRequest.getAttribute("business_app_id")%>'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>';
	function submitForm(actionParameter) {
		$("input[type='hidden'],input[type='text'], textarea").each(function(){
  		  convertSpecialCharactersHTMLGlobal($(this).attr("id"),true);
  	 	 });
		document.BoardQuestion.action = actionUrl + '&next_action=' + actionParameter;
		document.BoardQuestion.submit();
	}
</script>
<style>
select{
width: 250px;
}
* {
	padding: 0px;
}
</style>
</head>
<body Style="padding:0mm 0mm; margin:0cm 0cm 0cm 0cm " bgcolor="#FFFFFF">
  <div id="fb_main" class="class_main formcontainer">
    <form action="<portlet:actionURL/>" method="post" name="BoardQuestion">
      <input type="hidden" name="fb_formName" value="BoardQuestion"></input><input type="hidden" name="fb_fileName" value="Board"></input><input type="hidden" name="fb_formVersion" value="0"></input><input type="hidden" name="fb_language" value="English"></input><input type="hidden" name="fb_languageCode" value="en"></input><span id="property" password=" " username=" " driverclass=" " url=" " backgroundcolor="FFFFFF" filesize="2" fileunit="mb" makesqlfieldreadonly="false" convertdropdowntotextbox="false" defaultpermission="view" tablename="BOARD_FORM" name="property" class="class_0">
      </span>
      <div id="fb_divBl0ck1" class="class_1"></div>
      <div id="fb_divBl0ck2" class="class_2">
        <fb:line id="lineElt11341745818169dIi15v" name="div_row_q0" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="83" y1="11" x2="1670" x1="0">
          <div>
            <h2 style="float:none">Board Questions</h2>
          </div>
          <div style="min-height:3px; background-color:black; width:80%"></div>
          <span id="txtElt11340797376852dIi15v_textElement" class="class_12">
            <span class="class_13">
              <p>
                <span class="class_14"><font face="verdana,geneva">Please fill out the form below as accurately as possible. This information will determine what documents you are required to upload.</font></span>
              </p>
              <span class="class_15">
                <p class="class_16">
                  <span class="class_17"><font face="verdana,geneva">*</font></span>
                  <span class="class_18"><font face="verdana,geneva">Indicates required fields</font></span>
                </p>
              </span>
            </span>
          </span>
        </fb:line>
      </div>
      <div id="fb_divBl0ck3" class="class_19"></div>
      <div id="fb_divBl0ck4">
        <div id="errorEltDiv" y1="0" y2="0" x1="0" x2="0"></div>
      </div>
      <div id="fb_divBl0ck5" class="class_20"></div>
      <div id="fb_divBl0ck6" class="class_21">
        <fb:line id="lineElt11340797376840dIi15v" name="div_row_q1" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="118" y1="90" x2="1670" x1="0">
          <h3>Board Committees</h3>
        </fb:line>
      </div>
      <div id="fb_divBl0ck7" class="class_25"></div>
      <div id="fb_divBl0ck8" class="class_26">
        <fb:line id="lineElt21340797545933dIi15v" name="div_row_q2" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="213" y1="119" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt11340797740841dIi15v_label" title="Check all Board Committees that your organization has" tag="label" for="checkboxElt11340797740846dIi15v_checkbox" class="class_28">Check all Board Committees that your organization has:</label>
            </span>
            <span class="formfield">
              <div>
                <fb:input id="checkboxElt11340797740846dIi15v_checkbox" type="checkbox" name="bc_executive" value="Executive" editrole=",semi_edit,admin,edit" title="Executive" tabindex="1" viewrole=",read_only,semi_edit" errorcode=" bc_executive" schemaname="BC_EXECUTIVE" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340797545933dIi15v"></fb:input>
                <label>
                Executive
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt21340797827264dIi15v_checkbox" type="checkbox" name="bc_finance" value="Finance" editrole=",semi_edit,admin,edit" title="Finance" tabindex="2" viewrole=",read_only,semi_edit" errorcode=" bc_finance" schemaname="BC_FINANCE" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340797545933dIi15v"></fb:input>
                <label>
                Finance
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt21340798624317dIi15v_checkbox" type="checkbox" name="bc_audit" value="Audit" editrole=",semi_edit,admin,edit" title="Audit" tabindex="3" viewrole=",read_only,semi_edit" errorcode=" bc_audit" schemaname="BC_AUDIT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340797545933dIi15v"></fb:input>
                <label>
                Audit
                </label>
              </div>
              <!-- Start of changes for Release 3.10.0 . Enhancement 6572 -->
              <div>
                 <fb:input id="checkboxElt21340798624318dIi15v_checkbox" type="checkbox" name="bc_na" value="None of the above" editrole=",semi_edit,admin,edit" title="None of the above" tabindex="3" viewrole=",read_only,semi_edit" errorcode=" bc_na" schemaname="BC_NA" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340797545933dIi15v"></fb:input>
                 <label>
               	 None of the above
                 </label>
              </div>
              <!-- Start of changes for Release 3.10.0 . Enhancement 6572 -->
            </span>
            <span class="formfield error">
              <fb:error id="BC_EXECUTIVE" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck9" class="class_48"></div>
      <div id="fb_divBl0ck10" class="class_49">
        <fb:line id="lineElt11340798921893dIi15v" name="div_row_q3" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="241" y1="214" x2="1670" x1="0">
          <h3>Board Meetings</h3>
        </fb:line>
      </div>
      <div id="fb_divBl0ck11" class="class_53"></div>
      <div id="fb_divBl0ck12" class="class_54">
        <fb:line id="lineElt21340798923984dIi15v" name="div_row_q4" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="314" y1="242" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required" tag="span">*</span>
            <label id="labelElt1340799154043dIi15v_label" title="What is the minimum number of board meetings per year indicated by your organization's Corporate By-Laws or Equivalent?" tag="label" for="checkboxElt11340797740846dIi15v_checkbox" class="class_56">What is the minimum number of board meetings per year indicated by your organization's Corporate By-Laws or Equivalent?</label>
            </span>
            <span class="formfield">
              <fb:select id="dropdownElt11340798921896dIi15v_select" name="min_board_meetings" cssclass="class_59" optionsize="17" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="What is the minimum number of board meetings per year indicated by your organization's Corporate By-Laws or Equivalent?" tabindex="4" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="MIN_BOARD_MEETINGS" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt21340798923984dIi15v">
                <option value=" " title=" " selected="selected">Select</option>
                <option value="0" title="0">0</option>
                <option value="1" title="1">1</option>
                <option value="2" title="2">2</option>
                <option value="3" title="3">3</option>
                <option value="4" title="4">4</option>
                <option value="5" title="5">5</option>
                <option value="6" title="6">6</option>
                <option value="7" title="7">7</option>
                <option value="8" title="8">8</option>
                <option value="9" title="9">9</option>
                <option value="10" title="10">10</option>
                <option value="11" title="11">11</option>
                <option value="12" title="12">12</option>
                <option value="13" title="13">13</option>
                <option value="14" title="14">14</option>
                <option value="15" title="15">15</option>
              </fb:select>
            </span>
            <span class="formfield error">
              <fb:error id="MIN_BOARD_MEETINGS" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck13" class="class_61"></div>
      <div id="fb_divBl0ck14" class="class_62">
        <fb:line id="lineElt11340800580858dIi15v" name="div_row_q5" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="399" y1="315" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt1340800921902dIi15v_label" title="Confirm that a quorum of your Board of Directors has met the minimum number of times indicated by your organization's Corporate By-Laws or Equivalent" tag="label" for="dropdownElt11340800580861dIi15v_select">Confirm that a quorum of your Board of Directors has met the minimum number of times indicated by your organization's Corporate By-Laws or Equivalent</label>
            </span>
            <span class="formfield">
              <fb:select id="dropdownElt11340800580861dIi15v_select" name="con_bm" cssclass="class_66" optionsize="3" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="Confirm that a quorum of your Board of Directors has met the minimum number ot times indicated by your organization's Corporate By-Laws or Equivalent" tabindex="5" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="CON_BOARD_MEETINGS" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340800580858dIi15v">
                <option value=" " title=" " selected="selected">Select</option>
                <option value="Yes" title="Yes">Yes</option>
                <option value="No" title="No">No</option>
              </fb:select>
            </span>
            <span class="formfield error">
              <fb:error id="CON_BOARD_MEETINGS" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck15" class="class_68"></div>
      <div id="fb_divBl0ck16" class="class_69">
        <fb:line id="lineElt11340801027339dIi15v" name="div_row_q6" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="428" y1="400" x2="1670" x1="0">
          <h3>Duties</h3>
        </fb:line>
      </div>
      <div id="fb_divBl0ck17" class="class_73"></div>
      <div id="fb_divBl0ck18" class="class_74">
        <fb:line id="lineElt21340801045590dIi15v" name="div_row_q7" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="765" y1="429" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required" tag="span">*</span>
            <label id="labelElt1340806001989dIi15v_label" title="Check all the items your organization's Board of Directors reviews and/or approves" tag="label" for="checkboxElt11340801027343dIi15v_checkbox" class="class_76">Check all the items your organization's Board of Directors reviews and/or approves:</label>
            </span>
            <span class="formfield">
              <div>
                <fb:input id="checkboxElt11340801027343dIi15v_checkbox" type="checkbox" name="annual_budget" value="annual_budget" editrole=",semi_edit,admin,edit" title="Annual operating budget" tabindex="6" viewrole=",read_only,semi_edit" errorcode=" annual_budget" schemaname="ANNUAL_BUDGET" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340801045590dIi15v"></fb:input>
                <label>
                Annual operating budget
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt21340801250774dIi15v_checkbox" type="checkbox" name="policise_procedure" value="policise_procedure" editrole=",semi_edit,admin,edit" title="Policies and procedures" tabindex="7" viewrole=",read_only,semi_edit" errorcode=" policise_procedure" schemaname="POLICIES_PROCEDURE" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340801045590dIi15v"></fb:input>
                <label>
                Policies and procedures
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt21340801639219dIi15v_checkbox" type="checkbox" name="exec_performance" value="exec_performance" editrole=",semi_edit,admin,edit" title="Executive performance and compensation" tabindex="8" viewrole=",read_only,semi_edit" errorcode=" exec_performance" schemaname="EXEC_PERFORMANCE" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340801045590dIi15v"></fb:input>
                <label>                 
                Executive performance and compensation
                </label>             
              </div>
              <div>
                <fb:input id="checkboxElt11340802879576dIi15v_checkbox" type="checkbox" name="fund_raising" value="fund_raising" editrole=",semi_edit,admin,edit" title="Fundraising plan" tabindex="9" viewrole=",read_only,semi_edit" errorcode=" fund_raising" schemaname="FUND_RAISING" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340801045590dIi15v"></fb:input>
                <label>
                Fundraising plan
                </label>   
              </div>
              <div>
                <fb:input id="checkboxElt21340802935875dIi15v_checkbox" type="checkbox" name="internal_controls" value="internal_controls" editrole=",semi_edit,admin,edit" title="Internal controls, including financial controls" tabindex="10" viewrole=",read_only,semi_edit" errorcode=" internal_controls" schemaname="INTERNAL_CONTROLS" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340801045590dIi15v"></fb:input>
                <label>          
                Internal controls, including financial controls
                </label>  
              </div>
              <div>
                <fb:input id="checkboxElt21340803108860dIi15v_checkbox" type="checkbox" name="ann_ind_audit" value="ann_ind_audit" editrole=",semi_edit,admin,edit" title="Annual independent audit conducted by a Certified Public Accountant / Audited Financial statement" tabindex="11" viewrole=",read_only,semi_edit" errorcode=" ann_ind_audit" schemaname="ANN_IND_AUDIT" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340801045590dIi15v"></fb:input>
                <label>
                Annual independent audit conducted by a Certified Public Accountant / Audited Financial statement
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt11340803760739dIi15v_checkbox" type="checkbox" name="form_irs_990" value="form_irs_990" editrole=",semi_edit,admin,edit" title="IRS Form 990" tabindex="12" viewrole=",read_only,semi_edit" errorcode=" form_irs_990" schemaname="FORM_IRS_990" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340801045590dIi15v"></fb:input>
                <label>
                IRS Form 990
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt21340803825975dIi15v_checkbox" type="checkbox" name="prog_opp_outcomes" value="prog_opp_outcomes" editrole=",semi_edit,admin,edit" title="Program operations and outcomes" tabindex="13" viewrole=",read_only,semi_edit" errorcode=" prog_opp_outcomes" schemaname="PROG_OPP_OUTCOMES" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340801045590dIi15v"></fb:input>
                <label>
                Program operations and outcomes
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt21340804029855dIi15v_checkbox" type="checkbox" name="bank_acc" value="bank_acc" editrole=",semi_edit,admin,edit" title="Opening and closing of bank statements, lines of credits, and investment / divestment" tabindex="14" viewrole=",read_only,semi_edit" errorcode=" bank_acc" schemaname="BANK_ACC" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340801045590dIi15v"></fb:input>
                <label>
                Opening and closing of bank statements, lines of credits, and investment / divestment
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt11340805316197dIi15v_checkbox" type="checkbox" name="buy_sell" value="buy_sell" editrole=",semi_edit,admin,edit" title="Buying / selling of property" tabindex="16" viewrole=",read_only,semi_edit" errorcode=" buy_sell" schemaname="BUY_SELL" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340801045590dIi15v"></fb:input>
                <label>
                Buying / selling of property
                </label>
              </div>
              <div>
                <fb:input id="checkboxElt21340805345118dIi15v_checkbox" type="checkbox" name="non_of_above" value="non_of_above" editrole=",semi_edit,admin,edit" title="None of the above" tabindex="17" viewrole=",read_only,semi_edit" errorcode=" non_of_above" schemaname="DUTIES_NONE" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340801045590dIi15v"></fb:input>
                <label>
                None of the above
                </label>
              </div>
            </span>
            <span class="formfield error">
              <fb:error id="ANNUAL_BUDGET" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck19" class="class_144"></div>
      <div id="fb_divBl0ck20" class="class_145">
        <fb:line id="lineElt11340806190313dIi15v" name="div_row_q8" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="819" y1="766" x2="1670" x1="0">
          <div class="row">
            <span class="label">  
            <span class="required">*</span>
            <label id="labelElt1340806352909dIi15v_label" title="How often does your organization's Board of Directors review financial statements?" tag="label" for="dropdownElt1340806247129dIi15v_select" class="class_147">How often does your organization's Board of Directors review financial statements?</label>
            </span>
            <span class="formfield">
              <fb:select id="dropdownElt1340806247129dIi15v_select" name="review_financials" cssclass="class_149" optionsize="4" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="How often does your organization's Board of Directors review financial statements" tabindex="18" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="REVIEW_FINANCIALS" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340806190313dIi15v">
                <option value=" " title=" " selected="selected">Select</option>
                <option value="Quarterly" title="Quarterly">Quarterly</option>
                <option value="Annually" title="Annually">Annually</option>
                <option value="Other" title="Other">Other</option>
              </fb:select>
            </span>
            <span class="formfield error">
              <fb:error id="REVIEW_FINANCIALS" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck21" class="class_152"></div>
      <div id="fb_divBl0ck22" class="class_153">
        <fb:line id="lineElt21340806217582dIi15v" name="div_row_q9" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="929" y1="820" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required" tag="span">*</span>
            <label id="labelElt1340806616370dIi15v_label" title="If other, please specify" tag="label" for="textareaElt11340806455767dIi15v_textarea" class="class_155">If other, please specify:</label>
            </span>
            <span class="formfield" maxlength="150">
              <fb:textarea id="textareaElt11340806455767dIi15v_textarea" name="other_rev_finance" cssclass=" input class_157" value="" editrole=",semi_edit,admin,edit" title="If other, please specify" tabindex="19" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="OTHER_REVIEW_FINANCIALS" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340806217582dIi15v" cols="33" rows="2"></fb:textarea>
            </span>
            <span class="formfield error">
              <fb:error id="OTHER_REVIEW_FINANCIALS" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck23" class="class_160"></div>
      <div id="fb_divBl0ck24" class="class_161">
        <fb:line id="lineElt11340806744038dIi15v" name="div_row_q10" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="993" y1="930" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt1340806927709dIi15v_label" title="How often does your organization's Board of Directors review budget projections and / or cash flow projection?" tag="label" for="dropdownElt1340806784942dIi15v_select" class="class_163">How often does your organization's Board of Directors review budget projections and / or cash flow projection?</label>
            </span>
            <span class="formfield">
              <fb:select id="dropdownElt1340806784942dIi15v_select" name="budget_cash_projection" cssclass="class_165" optionsize="4" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="How often does your organization's Board of Directors review budget projections and / or cash flow projection?" events="budget_cash_projection" tabindex="20" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="BUDGET_CASH_PROJECTIONS" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt11340806744038dIi15v">
                <option value=" " title=" " selected="selected">Select</option>
                <option value="Annually" title="Annually">Annually</option>
                <option value="Quarterly" title="Quarterly">Quarterly</option>
                <option value="Other" title="Other">Other</option>
              </fb:select>
            </span>
            <span class="formfield error">
              <fb:error id="BUDGET_CASH_PROJECTIONS" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck25" class="class_168"></div>
      <div id="fb_divBl0ck26" class="class_169">
        <fb:line id="lineElt21340806900126dIi15v" name="div_row_q11" visibilityvalue="Hidden" condition="condition" lineid="undefined" sectionname=" " y2="1103" y1="994" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required" tag="span">*</span>
            <label id="labelElt1340807160474dIi15v_label" title="If other, please specify" tag="label" for="textareaElt1340807018741dIi15v_textarea" class="class_171">If other, please specify:</label>
            </span>
            <span class="formfield" maxlength="150">
              <fb:textarea id="textareaElt1340807018741dIi15v_textarea" name="cash_budget_other" cssclass=" input class_173" value="" editrole=",semi_edit,admin,edit" title="If other, please specify" tabindex="21" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="OTHER_CASH_BUDGET_PROJ" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" defaultpermission="view" lineid="lineElt21340806900126dIi15v" cols="33" rows="2"></fb:textarea>
            </span>
            <span class="formfield error">
              <fb:error id="OTHER_CASH_BUDGET_PROJ" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck27" class="class_176"></div>
      <div id="fb_divBl0ck28" class="class_177">
        <fb:line id="lineElt31340806984002dIi15v" name="div_row_q12" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="1170" y1="1104" x2="1670" x1="0">
          <div class="row">
            <span class="label">
            <span class="required">*</span>
            <label id="labelElt1340807347148dIi15v_label" title="What is the minimum number of trainings (including new member orientation) conducted annually for board members?" tag="label" for="dropdownElt1340807257809dIi15v_select" class="class_179">What is the minimum number of trainings (including new member orientation) conducted annually for board members?</label>
            </span>
            <span class="formfield">
              <fb:select id="dropdownElt1340807257809dIi15v_select" name="min_trainings" cssclass="class_182" optionsize="17" firstfield="Select" defaultvalue=" " editrole=",semi_edit,admin,edit" title="What is the minimum number of trainings (including new member orientation) conducted annually for board members?" events="min_trainings" tabindex="20" viewrole=",read_only,semi_edit" errorcode=" ! This field is required." schemaname="MIN_TRAININGS" nonerole=",read_only,semi_edit,admin,edit" defaultpermission="view" lineid="lineElt31340806984002dIi15v">
                <option value=" " title=" " selected="selected">Select</option>
                <option value="0" title="0">0</option>
                <option value="1" title="1">1</option>
                <option value="2" title="2">2</option>
                <option value="3" title="3">3</option>
                <option value="4" title="4">4</option>
                <option value="5" title="5">5</option>
                <option value="6" title="6">6</option>
                <option value="7" title="7">7</option>
                <option value="8" title="8">8</option>
                <option value="9" title="9">9</option>
                <option value="10" title="10">10</option>
                <option value="11" title="11">11</option>
                <option value="12" title="12">12</option>
                <option value="13" title="13">13</option>
                <option value="14" title="14">14</option>
                <option value="15" title="15">15</option>
              </fb:select>
            </span>
            <span class="formfield error">
              <fb:error id="MIN_TRAININGS" cssclass="error"></fb:error>
            </span>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck29" class="class_184"></div>
      <div id="fb_divBl0ck30" class="class_185">
        <fb:line id="lineElt11340807229449dIi15v" name="div_row_q13" visibilityvalue="Visible" condition="condition" lineid="undefined" sectionname=" " y2="1201" y1="1171" x2="1670" x1="0">
          <div class="buttonholder">
            <fb:button id="buttonElt11340807229452dIi15v_button" type="button" name="back" cssclass=" button graybtutton class_187" value=" &lt;&lt; Back " editrole=",semi_edit,admin,edit" title="Back" events="eventname" tabindex="20" viewrole=",read_only,semi_edit" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" lineid="lineElt11340807229449dIi15v" defaultpermission="view"></fb:button>
            <fb:button id="buttonElt11340807507869dIi15v_button" type="button" name="save" cssclass=" button class_190" value=" Save " editrole=",semi_edit,admin,edit" title="Save" events="eventname" tabindex="21" viewrole=",read_only,semi_edit" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" lineid="lineElt11340807229449dIi15v" defaultpermission="view"></fb:button>
            <fb:button id="buttonElt21340807519711dIi15v_button" type="button" name="save_next" cssclass=" button class_193" value=" Save &amp; Next" editrole=",semi_edit,admin,edit" title="Save &amp; Next" events="eventname" tabindex="23" viewrole=",read_only,semi_edit" nonerole=",read_only,semi_edit,admin,edit" makesqlfieldreadonly="false" lineid="lineElt11340807229449dIi15v" defaultpermission="view"></fb:button>
          </div>
        </fb:line>
      </div>
      <div id="fb_divBl0ck31" class="class_195"></div>
      <fb:input id="fbjscounter" type="hidden" name="fbjscounter" value="1"></fb:input>
    </form>
  </div>
</body>
</html>