package com.nyc.hhs.frameworks.security;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.PropertyLoader;

/**
 * @author manish.grover
 * @Descrition: This is a generic class to create JQGrid, on the basis of few
 *              attributes.
 * 
 */

public class JQGridTag extends BodyTagSupport
{
	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(JQGridTag.class);

	/**
	 * STR_JS_1 to create javascript for jqgrid
	 */
	private static final String STR_JS_1 = "<script type='text/javascript'> $(document).ready(function () {"
			+ "'use strict'; $('.ui-pg-div').live('mouseover',function(){$(this).css(\"cursor\",\"pointer\");});";

	/**
	 * STR_JS_2 to create javascript for jqgrid
	 */
	private static final String STR_JS_2 = "var gridPage=1;var globalUrl='';var headerData='';msgKey=''; var duplicateValue = '';var currentRowInfo = null;var parentTotal=new Object();var checkForTotalValueData = 0; var totalSign = totalLastCol.charAt(0); var totalColumn=''; var getEditableFields = true; "
			+ "var edtableRows=''; var colLen = 0; var lastsel, pager_id;"
			+ " var clickOnce = true; var subgrid_table_id";

	/**
	 * STR_JS_3 to create javascript for jqgrid
	 */
	private static final String STR_JS_3 = ";var currentId = '';var applyStyle = true;var editButton = false; var editButtonData = ''; var myEditOptions = {keys: true,oneditfunc: function (rowid) {"
			+ "if(dropDownData != 'null'){"
			+ "$('select option').each(function(){"
			+ "		var title = $(this).html().replace(/&amp;/g, '&');"
			+ "		$(this).attr('title', title);"
			+ "});"
			+ "}"
			// Start: Added in Release 6
			+ "if(exportFileName != 'null'){$(\"input[name='internalTitle']\").fieldFormatterSpecial(\"XXXXXXX\", \"A\", \"\");"
			+ "	setTimeout(function() {$('td#'+subgrid_table_id+'_ilexport').removeClass().addClass('ui-pg-button ui-corner-all ui-state-disabled');},100);}"
			// End: Added in Release 6
			+ "$('#p_'+subgrid_table_id+'_center>table').addClass('ui-state-disabled');"
			+ "	$('#p_'+subgrid_table_id+'_center>table>tbody>tr>td:eq(3)>input').attr('disabled','disabled');"
			+ "if(notAllowDuplicate != 'null'){if(notAllowDuplicate.split(',')[0] == 'uobc'){$(\"input[name='uobc']\").alphanumeric( { allow: \"-.' \" , nchars:\"_\"});$(\"input[name='uobc']\").fieldFormatterSpecial(\"XXX-XXXX-XXXX\",\"A\",\"\");$(\"input[name='subOC']\").alphanumeric( { allow: \"-.' \" , nchars:\"_\"});$(\"input[name='subOC']\").fieldFormatterSpecial(\"XXXX\",\"A\",\"\");$(\"input[name='rc']\").alphanumeric( { allow: \"-.' \" , nchars:\"_\"});$(\"input[name='rc']\").fieldFormatterSpecial(\"XXXXXX\",\"A\",\"\");}}"
			+ "if(negativeCurrency != 'null'){ var tmpNeg = negativeCurrency.split(','); for(var i=0; i<tmpNeg.length; i++){$('#'+rowid+'_'+tmpNeg[i]).autoNumeric( {aSep: '', vMin: '-9999999999999999.99', vMax: '9999999999999999.99'} );}}"
			+ "if(positiveCurrency != 'null'){var tmpPos = positiveCurrency.split(','); for(var i=0; i<tmpPos.length; i++){$('#'+rowid+'_'+tmpPos[i]).autoNumeric( {aSep: '', vMax: '9999999999999999.99'} );}}"
			+ "currentId = rowid;"
			+ "if(rowid == 'new_row'){"
			+ "if(!isArrayContainsDuplicateValue(clickOnGridArr,subgrid_table_id)){"
			+ "	clickOnGridArr.push(subgrid_table_id);"
			+ "}"
			+ "				$('#' + subgrid_table_id+ '>tbody>#'+rowid+'>td').each(function(i) {"
			+ "				$(this).attr('title','');		if($(this).html() == '0.00' || $(this).html() == '&nbsp;'){"
			+ "				$(this).html(''); "
			+ "			}else{"
			+ "				if($(this).find('input').val() == '.00' || $(this).find('input').val() == '0.00'){"
			+ "					$(this).find('input').val(''); "
			+ "				}else if($(this).find('input').val() == ''){"
			+ "					$(this).find('input').val(''); "
			+ "				}"
			+ "			}"
			+ "	});"
			+ "}"
			+ "else if(editButton){"
			+ "editPage();"
			+ "if(!isArrayContainsDuplicateValue(clickOnGridArr,subgrid_table_id)){"
			+ "	clickOnGridArr.push(subgrid_table_id);"
			+ "}"
			+ "$('td#del_'+ subgrid_table_id).removeClass().addClass('ui-pg-button ui-corner-all ui-state-disabled');currentRowInfo = null;"
			+ "	$('#' + subgrid_table_id + '>tbody>#'+ rowid + '>td').each(function(i) {"
			+ "		var editTemp = edtableRows.split(',');"
			+ "		for ( var i = 0; i < editTemp.length; i++) {"
			+ "			var tempCol = editTemp[i].split(':');"
			+ "		if (tempCol[0] == getColName(this)) {"
			+ "			if (tempCol[1] == 'true') {"
			+ "				var tmp = 0;"
			+ "				tmp = $(this).find('input').val();"
			+ "				if (tmp != undefined) {"
			+ "if (editButtonData == '') {"
			+ "	editButtonData = tempCol[0] + ':' + tmp;"
			+ "} else {"
			+ "editButtonData = editButtonData + ',' + tempCol[0] + ':' + tmp;"
			+ "}"
			+ "					if (tmp.indexOf('$') == 0) {"
			+ "						var oldVal = $(this).find('input').val().replace(')', '');"
			+ "						var newVal = oldVal.replace('$', '-');"
			+ "						$(this).find('input').val(newVal);"
			+ "}else if (tmp[tmp.length - 1] === '%') {"
			+ "	$(this).find('input').val(tmp.replace('%',''));"
			+ "}"
			+ "			}"
			+ "		}getCurrEditRowVal(this);	"
			+ "	}"
			+ "	}"
			+ "			"
			+ "		});"
			+ "	}"
			+ "},"
			+ "afterrestorefunc : function(rowid){"
			// Start: Added in Release 6
			+ "if(exportFileName != 'null'){"
			+ "	$('td#'+subgrid_table_id+'_ilexport').removeClass().addClass('ui-pg-button ui-corner-all');"
			+ "}"
			// End: Added in Release 6
			+ " if($('#p_'+ subgrid_table_id+ '_center>table>tbody>tr>td:eq(3)>span').html() == 0){"
			+ "	setTimeout(function() {"
			+ "		$('#p_' + subgrid_table_id + '_center>table>tbody>tr>td:eq(5)').addClass('ui-pg-button ui-corner-all ui-state-disabled');"
			+ "		$('#p_' + subgrid_table_id + '_center>table>tbody>tr>td:eq(6)').addClass('ui-pg-button ui-corner-all ui-state-disabled');"
			+ "	},100);"
			+ "}"
			+ "$('#p_'+subgrid_table_id+'_center>table').removeClass('ui-state-disabled');"
			+ "$('#p_'+subgrid_table_id+'_center>table>tbody>tr>td:eq(3)>input').removeAttr('disabled');"
			+ "clickOnGridArr = removeValue(clickOnGridArr, subgrid_table_id);"
			+ "if (editButton) {"
			+ "	var editTemp = editButtonData.split(',');"
			+ "	for ( var i = 0; i < editTemp.length; i++) {"
			+ "		var tempCol = editTemp[i].split(':');"
			+ "		$('#' + subgrid_table_id+ '>tbody>#' + rowid+ '>td').each(function(i) {"
			+ "							if (getColName(this) == tempCol[0]) {"
			+ "								if (tempCol[1].indexOf('$') == 0) {"
			+ "									$(this).html(tempCol[1].replace('$','-').replace(')','')).formatCurrency();"
			+ "								} else if (tempCol[1][tempCol[1].length - 1] === '%') {"
			+ "									$(this).html(tempCol[1]);"
			+ "								}"
			+ "							}"
			+ "						});"
			+ "	}"
			+ "	editButtonData = '';"
			+ "}"
			+ "editButton = true;"
			+ "$("
			+ "		'#' + subgrid_table_id + '>tbody>#'"
			+ "				+ rowid + '>td')"
			+ "		.each("
			+ "				function(i) {"
			+ "					if ($(this).html().indexOf("
			+ "							'$-') == 0) {"
			+ "						var oldVal = $(this)"
			+ "								.html()"
			+ "								.replace('$-',"
			+ "										'($');"
			+ "						var newVal = oldVal"
			+ "								+ ')';"
			+ "						$(this).html(newVal);"
			+ "					}"
			+ "				});"
			+ "if(rowid == 'new_row'){setTimeout(function(){$('td#'+subgrid_table_id+";

	/**
	 * STR_JS_4 to create javascript for jqgrid
	 */
	private static final String STR_JS_4 = "'_iledit').removeClass().addClass('ui-pg-button ui-corner-all ui-state-disabled');},100);}"
			+ "if(rowid != 'new_row' && isNewRecordDelete != 'null' && isNewRecordDelete == 'true' && rowid.indexOf('_newrecord') !== -1){ $('td#del_'+ subgrid_table_id).removeClass('ui-pg-button ui-corner-all ui-state-disabled');}"
			+ "else if(rowid != 'new_row' && isNewRecordDelete == 'null' && isNewRecordDelete != 'true'){  $('td#del_'+ subgrid_table_id).removeClass('ui-pg-button ui-corner-all ui-state-disabled');  }"
			+ "if (dropDownData != 'null') {"
			+ "	var title = $('#' + subgrid_table_id + '>tbody>#'+ rowid + '>td').html().replace(/&amp;/g, '&');"
			+ "	$('#' + subgrid_table_id + '>tbody>#'+ rowid + '>td:eq(0)').attr('title',title);"
			+ "}"
			+ "},"
			+ "aftersavefunc: function (rowid, response, options) { globalUrl=''; pageGreyOut(); applyStyle = true; "
			+ "clickOnGridArr = removeValue(clickOnGridArr, subgrid_table_id);" + "$('#table_";

	/**
	 * STR_JS_5 to create javascript for jqgrid
	 * 
	 * Updated in Release 6
	 */
	private static final String STR_JS_5 = "}}; var numberTemplate = {	align : 'center',formatter : 'number',formatoptions : {prefix : '',suffix : '',thousandsSeparator : ''}}; var integerTemplate = {	align : 'center',formatoptions : {prefix : '',suffix : '',thousandsSeparator : ''}}; var currencyTemplate = {align: 'right', "
			+ "formatter : currencyFmatter,unformat : unformatCurrency};"
			// Start: Added in Release 6
			+ "var blankTemplate = {formatter:blankFmatter};function blankFmatter(cellvalue, options, rowObject) {return '';}"
			// Start: Added for Defect - 8501
			+ "var numberCommaFormatTemplate = {align: 'center',formatter: 'number', formatoptions: {prefix: '',suffix: '',thousandsSeparator: ','}};"
			// End: Added for Defect - 8501
			// End: Added in Release 6
			// Start: Added for Defect - 9308 R 7.12.0
			+ "var integerCommaFormatTemplate = {align: 'center', formatter: 'integer', formatoptions: {prefix: '',suffix: '',thousandsSeparator: ','}};"
			// End: Added for Defect - 9308 R 7.12.0
			// Start: Added in R7
			+ "var negativeModificationTemplate = {align: 'right',formatter : negativeMoCurrencyFmatter,unformat : unformatCurrency};"
			// End: Added in Release 7
			+ "var  percentageTemplate = {align : 'center',formatter : 'number',formatoptions : {prefix : '',suffix : '%',thousandsSeparator : ''}};var checkForZeroAndDelete =\"";

	/**
	 * STR_JS_6 to create javascript for jqgrid
	 */
	private static final String STR_JS_6 = "msgKey = '";

	/**
	 * STR_JS_6_1 to create javascript for jqgrid
	 */
	private static final String STR_JS_6_1 = "var dropDownData = \"";

	/**
	 * RELOAD to create javascript for jqgrid
	 */
	private static final String RELOAD = "').trigger('reloadGrid');";

	/**
	 * FUNCTION_1 to create javascript for jqgrid
	 */
	private static final String FUNCTION_1 = "function getCurrEditRowVal(obj){"
			+ "if (currentRowInfo == null) {"
			+ "if(typeof $(obj).find('input').val() != 'undefined'){"
			+ "	currentRowInfo = getColName(obj) + ':'"
			+ "	+ $(obj).find('input').val().replace(/,/g,'');"
			+ "}else{"
			+ "	currentRowInfo = getColName(obj) + ':' + $(obj).html().replace(/,/g,'');"
			+ "}"
			+ "} else {"
			+ "if(typeof $(obj).find('input').val() != 'undefined'){"
			+ "currentRowInfo = currentRowInfo + ','+ getColName(obj) + ':' + $(obj).find('input').val().replace(/,/g,'');"
			+ "}else{"
			+ "	currentRowInfo = currentRowInfo + ',' + getColName(obj) + ':' + $(obj).html().replace(/,/g,'');"
			+ "}"
			+ "}"
			+ "}"
			+ "function editPage(){"
			+ "	if($('#p_' + subgrid_table_id + '_center>table>tbody>tr>td:eq(3)>.ui-pg-input').is(':visible')){"
			+ "			var pageVal = $('#p_' + subgrid_table_id + '_center>table>tbody>tr>td:eq(3)>.ui-pg-input').val() + '&';"
			+ "			var tempUrl =jQuery('#' + subgrid_table_id).getGridParam('editurl');"
			+ "			var urlData ='';"
			+ "			if(globalUrl != ''){"
			+ "				jQuery('#' + subgrid_table_id).setGridParam({cellurl:tempUrl.substring(0,tempUrl.lastIndexOf('=') + 1)+pageVal});"
			+ "				jQuery('#' + subgrid_table_id).setGridParam({editurl:tempUrl.substring(0,tempUrl.lastIndexOf('=') + 1)+pageVal});"
			+ "			}else{"
			+ "				urlData = '&page=' + pageVal;"
			+ "				jQuery('#' + subgrid_table_id).setGridParam({cellurl:tempUrl+urlData});"
			+ "				jQuery('#' + subgrid_table_id).setGridParam({editurl:tempUrl+urlData});"
			+ "				globalUrl = pageVal;"
			+ "			}"
			+ "	}"
			+ "}"
			+ "function currencyFmatter(cellvalue, options, rowObject) {"
			+ "	if(typeof  cellvalue == 'undefined'){"
			+ "		cellvalue = '';return cellvalue;"
			+ "	}"
			+ "	return formatCurrency(cellvalue);"
			+ "}"
			//Start: Added in R7
			+ "function negativeMoCurrencyFmatter(cellvalue, options, rowObject) {"
			+ "	if(typeof  cellvalue == 'undefined'){"
			+ "	cellvalue = '';"
			+ "	return cellvalue;"
			+ "	}"
			+ " modFlag[options.pos+1] = '1';"
			+ " if(parseInt(cellvalue)<0){"
			//add set tool tip on header:replace mod_table_id
			+ " setTooltipsOnColumnHeader($(\"#table_mod_table_id \"),options.pos+1, 'When Income is earned beyond the Approved FY Budget, Remaining Amount will display a negative value.');"
			+ "	options.colModel.cellattr = negativeModification;}"
			+ "	return formatCurrency(cellvalue);"
			+ "}"
			//End: Added in R7
			+ "function delimitNumbers(str) {"
			+ "	return (str + \"\").replace(/(\\d+)((\\.\\d+)*)/g,"
			+ "	function(a, b, c) {"
			+ "		return (b.charAt(0) > 0&& !(c || \".\").lastIndexOf(\".\") ? b.replace(/(\\d)(?=(\\d{3})+$)/g,'$1,'): b)+ c;"
			+ "	});"
			+ "}"
			+ "function formatCurrency(cellvalue) {"
			+ "	var currType = '';"
			+ "	var tmp = '';"
			+ "	if ($.isNumeric(cellvalue)) {"
			+ "		currType = '$';"
			+ "		cellvalue = delimitNumbers(cellvalue);"
			+ "		if (cellvalue.indexOf('.') !== -1) {"
			+ "			var valAfterDec = cellvalue.substring(cellvalue.lastIndexOf('.') + 1);"
			+ "			if (valAfterDec < 10) {"
			+ "				tmp = '';"
			+ "			}"
			+ "			cellvalue = cellvalue + tmp;"
			+ "		} else {"
			+ "			cellvalue = cellvalue + '.00';"
			+ "		}"
			+ "	} else {"
			+ "		cellvalue = '';"
			+ "	}"
			+ "	tmp = '';"
			+ "	if(cellvalue.charAt(0) == '.'){"
			+ "		currType+='0';"
			+ "	}"
			+ "	if(cellvalue.substring(cellvalue.lastIndexOf('.') + 2) == ''){"
			+ "		tmp = '0';"
			+ "	}"
			+ "var result = currType + cellvalue + tmp;"
			+ "if(result.indexOf('-') !== -1){"
			+ "		result = '(' + result.replace('-','') + ')';"
			+ "}"
			+ "	return result;"
			+ "}"
			+ "function unformatCurrency(cellvalue, options) {"
			+ "	return cellvalue.replace('($', '-').replace('$', '').replace(')', '').replace(/,/g, '');"
			+ "}"
			+ "function headerNameDuplicateFound(tmp){"
			+ "	var i = headerName.split('|').length;"
			+ "	while (i--) {"
			+ "		if (headerName.split('|')[i].indexOf(tmp) !== -1)"
			+ "			return false;"
			+ "	}"
			+ "	return true;"
			+ "}"
			+ "function  isArrayContainsDuplicateValue(arr, findValue) {"
			+ " if (isGridReadOnly == 'false'){"
			+ "    var i = arr.length;"
			+ "    while (i--) {"
			+ "        if (arr[i].indexOf(findValue)!=-1) return true;"
			+ "    }"
			+ "    return false;}else{return true;}"
			+ "}"
			+ "function getSubGridIds(rowid){ var tempString='';"
			+ "$('#'+subgrid_table_id+' #'+rowid+'>td').each(function(i){"
			+ "var finalId = getColName(this);if(tempString != ''){"
			+ "tempString = tempString + ','+ finalId;}else{tempString = finalId;}});return tempString;}"
			+ "function notAllowDuplicateforOther(value, colname){"
			+ "var tmmm = new RegExp(inValidSymbolRegex);"
			+ "if(!tmmm.test(value)){"
			+ "		return [false,getHeaderNameFromColname(colname)+ \": Non-Acceptable Characters\" ]; } "
			+ "value = $.trim(value.toLowerCase());"
			+ "var isDuplicate = false;"
			+ "if(value != 'other'){"
			+ "for ( var i = 0; i < parentTotal.rows.length-1; i++) {"
			+ "var notAllowDuplicateTemp = notAllowDuplicate.split(',');"
			+ "var checkVal = $.trim(parentTotal.rows[i][colname].toLowerCase());"
			+ "if(value == checkVal){"
			+ "isDuplicate = true;"
			+ "break;"
			+ "}"
			+ "}"
			+ "if(isDuplicate){"
			+ "		    return [ false, colname+\":containForOtherDuplicateValues\" ];"
			+ "}else{"
			+ "return [ true, \"\" ];"
			+ "}"
			+ "}"
			+ "return [ true, \"\" ];"
			+ "}"
			+ "function allowOnlyPositiveValue(value, colname) {if ($.trim(value) == '') {return [false,getHeaderNameFromColname(colname)+ \": Field is required\" ];}"
			+ " if(value.indexOf('10000000000000000') !== -1){"
			+ " return [ false, colname+\": amountgreaterthen10000000000000000\" ];}"
			+ "if (checkForTotalValue != '' && colname == checkForTotalValue[0]) {"
			+ "	var tmpCurrInfo = currentRowInfo.split(',');"
			+ "	for(var count=0; count<tmpCurrInfo.length; count++){"
			+ "		if (tmpCurrInfo[count].split(':')[0].toLowerCase().replace(/\\s/g, '') == checkForTotalValue[1].toLowerCase().replace(/\\s/g, '')){"
			+ "			var checkVal = parseFloat(value) - parseFloat(tmpCurrInfo[count].split(':')[1].replace('$',''));"
			+ "			if( checkVal > 0){"
			+ "				msgKey = checkForTotalValue[2];"
			+ "				return [ false, \": allowOnlyPositiveValue\" ];	"
			+ "			}"
			+ "		}"
			+ "	}"
			+ "checkForTotalValueData = parseFloat(checkForTotalValueData) + parseFloat(value);"
			+ "if (checkForTotalValueData < 0) {"
			+ "	return [ false, \"+\" ];"
			+ "}"
			+ "}"
			+ "	if (value < 0) {"
			+ "		return [ false, colname+\": allowOnlyPositiveValue\" ];"
			+ "	} else if(value >= 0) {"
			+ "		return [ true, \"\" ];"
			+ "	}"
			+ "}"
			+ "function allowOnlyNegativeValue(value, colname) {if ($.trim(value) == '') {return [false,getHeaderNameFromColname(colname)+ \": Field is required\" ];}"
			+ "if (value.indexOf('-10000000000000000') !== -1) {"
			+ "	return [false,colname+ \": amountlesserthen10000000000000000\" ];"
			+ "}"
			+ "			if (value.indexOf('10000000000000000') !== -1) {"
			+ "				return [false,colname+ \": amountgreaterthen10000000000000000\" ];"
			+ "			}"
			+ "if(checkForTotalValue != '' && colname == checkForTotalValue[1]){"
			+ "	checkForTotalValueData = parseFloat(checkForTotalValueData) + parseFloat(value);"
			+ "	if(checkForTotalValueData < 0){"
			+ "		return [ false, \"+\" ];"
			+ "	}"
			+ "}"
			+ "	if (value > 0) {"
			+ "		return [ false, colname+\": allowOnlyNegativeValue\" ];"
			+ "	} else {"
			+ "		return [ true, \"\" ];"
			+ "	}"
			+ "}"
			+ "function allowBothSignCurrencyValue(value, colname){"
			+ "			if (value.indexOf('-10000000000000000') !== -1) {"
			+ "				return [false,colname + \": amountlesserthen10000000000000000\" ];"
			+ "			}"
			+ "			else if (value.indexOf('10000000000000000') !== -1) {"
			+ "				return [false,colname+ \": amountgreaterthen10000000000000000\" ];"
			+ "	} else {"
			+ "		return [ true, \"\" ];"
			+ "	}"
			+ "		}"
			+ "function allowOnlyPercentValue(value, colname){"
			+ "if($.isNumeric(value)){"
			+ "	if (value > 100 || value < 0) {"
			+ "		return [ false, colname+\"ExceedPercentageValue\" ];"
			+ "	} else {"
			+ "		return [ true, \"\" ];"
			+ "	}"
			+ "}else{"
			+ "	return [ false, colname+\"IsNotAPercentageFormat\" ];"
			+ "}"
			+ "}"
			+ "function isCOFDuplicate(value, colname){"
			+ "if(value.length<13){return [ false, \"valuesuobcinvalid\" ];}"
			+ "if(parentTotal.rows.length >= 1){duplicateValue = '';"
			+ "$('#'+ subgrid_table_id+ '>tbody>#'+ currentId + '>td').each(function(i) {"
			+ "if(i<3){"
			+ "	if(duplicateValue != ''){"
			+ "		duplicateValue = duplicateValue + ','+ $.trim($(this).find('input').val().toLowerCase());"
			+ "	}else{"
			+ "		duplicateValue = $.trim($(this).find('input').val().toLowerCase());"
			+ "	}	"
			+ "}"
			+ "});"
			+ "var isDuplicate = '';"
			+ "for ( var i = 0; i < parentTotal.rows.length; i++) {"
			+ "		var notAllowDuplicateTemp = notAllowDuplicate.split(',');"
			+ "		var checkVal = $.trim(parentTotal.rows[i][notAllowDuplicateTemp[0]].toLowerCase())+','+$.trim(parentTotal.rows[i][notAllowDuplicateTemp[1]].toLowerCase())+','+$.trim(parentTotal.rows[i][notAllowDuplicateTemp[2]].toLowerCase());"
			+ "		if(duplicateValue == checkVal){"
			+ "if(currentRowInfo != null){"
			+ "			var currentRowInfoTemp = currentRowInfo.split(',');"
			+ "			if(duplicateValue == $.trim(currentRowInfoTemp[0].split(':')[1].toLowerCase())+','+$.trim(currentRowInfoTemp[1].split(':')[1].toLowerCase()).replace('&nbsp;','')+','+$.trim(currentRowInfoTemp[2].split(':')[1].toLowerCase()).replace('&nbsp;','')){"
			+ "				isDuplicate = false; break;	}else{"
			+ "				isDuplicate = true;	 break;"
			+ "			}"
			+ "	}isDuplicate = true; break;	}		else{"
			+ "			isDuplicate = false; }"
			+ " 	}"
			+ " if(isDuplicate){"
			+ "return [ false, colname+\":containDuplicateValues\" ];}else{return [ true, \"\" ];}}  else {return [ true, \"\" ];}}"

			+ "function isMandatoryField(value, colname) {" + "	if ($.trim(value) == '') {"
			+ "		return [false,getHeaderNameFromColname(colname)+ \": Field is required\" ]; " + "}else{"
			+ "var tmmm = new RegExp(inValidSymbolRegex);" + "if(!tmmm.test(value)){"
			+ "		return [false,getHeaderNameFromColname(colname)+ \": Non-Acceptable Characters\" ]; } }"
			+ "	return [ true, \"\" ]; }" + "function getHeaderNameFromColname(colname) {"
			+ "	for ( var count = 0; count < headerData.split('|').length; count++) {"
			+ "		if (headerData.split('|')[count].split(':')[1] == colname) {"
			+ "			return headerData.split('|')[count].split(':')[0];" + "		}" + "	}" + "}"
			+ "function removeValue(arr, value) {" + "    for(var i = 0; i < arr.length; i++) {"
			+ "        if(arr[i] === value) {" + "arr.splice(i, 1);" + "break;}" + "  }" + "return arr;}"
			+ "function updateParentGrid(){" + "	if (colLen > 0) {" + "		for ( var i = 1; i <= colLen; i++) {"
			+ "			var checkNo = $('#table_";

	/**
	 * FUNCTION_2 to create javascript for jqgrid
	 */
	private static final String FUNCTION_2 = ">tbody>tr:nth-child(2)>td:nth-child('+ (i + 1)+ ')').html();"
			+ "			checkNo = checkNo.replace('$','').replace(/,/g,'').replace('(', '').replace(')', '').replace('%','');"
			+ "			if ($.isNumeric(checkNo)) {" + "				$('#table_";

	/**
	 * FUNCTION_3 to create javascript for jqgrid updated in R6
	 */
	private static final String FUNCTION_3 = ">tbody>tr:nth-child(2)>td:nth-child('+ (i + 1) + ')').html(0);" + "}"
			+ "}" + "}" + "var tblLen = $('#' + subgrid_table_id + '>tbody>tr').length;"
			// Start: Updated in Release 6
			+ "if (totalLastCol != '' && totalLastCol.indexOf('%') === -1) {" + "totalLastColumn(tblLen);" + "}"
			// End: Updated in Release 6
			+ "var rows = edtableRows.split(',');" 
			//[Start]R 7.2.0 QC7047
			+ "var advValTemp = []; "
			//[End]R 7.2.0 QC7047
			+ "for(var count = 1; count <= rows.length; count++ ){"
			+ "var topVal = $.trim($('#table_"
	
			;

	/**
	 * FUNCTION_4 to create javascript for jqgrid
	 */
	private static final String FUNCTION_4 = ">tbody>tr:nth-child(2)>td:nth-child('+ (count + 1) + ')').html());"
			+ "topVal = topVal.replace('$','').replace('($','').replace(')','').replace('%','').replace(/,/g, '');"
			+ "if($.isNumeric(topVal)){" + "	var formatType = '';"
			+ "	var tmpVal = $('#' + subgrid_table_id+ '>tbody>tr:nth-child(2)>td:nth-child('+(count)+')').html();"
			+ "if(tmpVal != null){" + "	if(tmpVal.indexOf('$') !== -1){" + "		formatType = 'currency';"
			+ "	}else if(tmpVal.indexOf('%') !== -1){" + "		formatType = 'percentage';" + "	}else{"
			+ "		formatType = 'nothing';" + "	}" + "	var rowTemp = rows[count-1].split(':');" + "	var total =0;"
			+ "	for(var i = 0; i < parentTotal.rows.length; i++ ){"
			+ "if(typeof parentTotal.rows[i][rowTemp[0]] != 'undefined' && parentTotal.rows[i][rowTemp[0]] != 'null'){"
			+ "		total = new Big(total).plus(new Big(parentTotal.rows[i][rowTemp[0]]));" + "		}" + " }"
        //[Start]R 7.2.0 QC7047
			+ " advValTemp.push(total); "
		//[End]R 7.2.0 QC7047
			+ "	if(formatType == 'currency'){" 
			//Start: Added in R7
			+ " if(modFlag[count] == 1 && total <0){" + "	$('#table_";
	
	private static final String FUNCTION_X = ">tbody>tr:nth-child(2)>td:nth-child(' + (count + 1) + ')').attr('style',"
			+ "'font-weight:bold;text-align:right;background:mediumaquamarine');} else{" 
			//End:Added in R7
			+ "$('#table_";

	/**
	 * FUNCTION_5 to create javascript for jqgrid
	 */
	private static final String FUNCTION_5 = ">tbody>tr:nth-child(2)>td:nth-child('+ (count + 1) + ')').attr('style',"
			+ "'font-weight:bold;text-align:right');}"
			+ "$('#table_";

	/**
	 * FUNCTION_6 to create javascript for jqgrid
	 */
	private static final String FUNCTION_6 = ">tbody>tr:nth-child(2)>td:nth-child('+ (count + 1) + ')').text(formatCurrency(total));"
			+ "	}else if(formatType == 'percentage')" 
	        //[Start]R 7.2.0 QC7047
			+ "{  if( advValTemp.length > 2 ) {  total =  ((advValTemp[1]/advValTemp[0])*100)    ; }" 
			+ "else { total = total/ parentTotal.rows.length;  }"
	        //[End]R 7.2.0 QC7047
			+ "    $('#table_"
    ;	
	
	
	/**
	 * FUNCTION_7 to create javascript for jqgrid
	 */
	private static final String FUNCTION_7 = ">tbody>tr:nth-child(2)>td:nth-child('+ (count + 1) + ')').html(total.toFixed(2) + '%');"
			+ "	}else{		$('#table_";

	/**
	 * FUNCTION_8 to create javascript for jqgrid updated in R6
	 */
	private static final String FUNCTION_8 = ">tbody>tr:nth-child(2)>td:nth-child('+ (count + 1) + ')').html(total.toFixed(2));"
			+ "	}	$('#table_";

	/**
	 * FUNCTION_9 to create javascript for jqgrid
	 */
	private static final String FUNCTION_9 = ">tbody>tr:nth-child(2)>td:nth-child('+ (count + 1) + ')').attr(\"title\",$('#table_";

	/**
	 * FUNCTION_10 to create javascript for jqgrid
	 */
	private static final String FUNCTION_10 = ">tbody>tr:nth-child(2)>td:nth-child('+ (count + 1) + ')').text());"
			+ "}}else if(topVal == '&nbsp;' || topVal == ''){" + "	$('#table_";

	/**
	 * FUNCTION_11 to create javascript for jqgrid Updated in R6
	 */
	private static final String FUNCTION_11 = ">tbody>tr:nth-child(2)>td:nth-child('+ (count + 1) + ')').attr('style','background:lightgrey;');"
			+ "}"
			+ "}"
			
			+ "	if(totalLastCol != ''){"
			// Start: Updated in Release 6
			+ " if(totalLastCol.indexOf('%') === -1){"
			+ "var tempTotalLastColTopVal = totalLastCol.split(',');"
			+ "var totColumn = 0;" + "for(var i=0; i<tempTotalLastColTopVal.length; i++){" + "$('#table_";
	// End: Updated in Release 6
	/**
	 * FUNCTION_12 to create javascript for jqgrid
	 */
	private static final String FUNCTION_12 = ">tbody>tr:nth-child(2)>td').each(function(eachCount) {"
			+ "	var sign = tempTotalLastColTopVal[i].charAt(0);"
			+ "	if(sign == '-'){"
			+ "		if(tempTotalLastColTopVal[i].replace('-', '') == getColName(this)){"
			+ "totColumn = new Big(totColumn).minus(new Big($(this).html().replace('(','-').replace(')','').replace('$','').replaceAll(',','')));"
			+ "		}"
			+ "	}else{"
			+ "		if(tempTotalLastColTopVal[i] == getColName(this)){"
			+ "totColumn = new Big(totColumn).plus(new Big($(this).html().replace('(','-').replace(')','').replace('$','').replaceAll(',','')));"
			+ "		}" + "	}" + "});" + "}" + "$('#table_";

	/**
	 * FUNCTION_13 to create javascript for jqgrid
	 */
	private static final String FUNCTION_13 = ">tbody>tr:nth-child(2)>td:nth-child('+ (rows.length + 1) + ')').html(formatCurrency(totColumn));"
			+ "$('#table_";

	/**
	 * FUNCTION_14 to create javascript for jqgrid
	 */
	private static final String FUNCTION_14 = ">tbody>tr:nth-child(2)>td:nth-child('+ (rows.length + 1) + ')').attr(\"title\",$('#table_";

	/**
	 * FUNCTION_15 to create javascript for jqgrid Updated in R6
	 */
	private static final String FUNCTION_15 = ">tbody>tr:nth-child(2)>td:nth-child('+ (rows.length + 1) + ')').text());"
			+ "}";

	// Start Added in Release 6
	/**
	 * FUNCTION_15_1 to create javascript for jqgrid Added in Release 6
	 */
	private static final String FUNCTION_15_1 = "  else{ var dividend = unformatCurrency($('#table_";

	/**
	 * FUNCTION_15_2 to create javascript for jqgrid Added in Release 6
	 */
	private static final String FUNCTION_15_2 = ">tbody>tr:nth-child(2)').find('td').eq(getColumnIndex(rows,totalLastCol.split(',')[0])).html(),null);var divisor = unformatCurrency($('#table_";

	/**
	 * FUNCTION_15_3 to create javascript for jqgrid Added in Release 6 Updated
	 * for Defect-8440
	 */
	private static final String FUNCTION_15_3 = ">tbody>tr:nth-child(2)').find('td').eq(getColumnIndex(rows,totalLastCol.split(',')[1])).html(),null);var result = dividend/divisor *100;if(result == 'Infinity' || isNaN(result)){result=0.00;}$('#table_";

	/**
	 * FUNCTION_15_4 to create javascript for jqgrid Added in Release 6
	 */
	private static final String FUNCTION_15_4 = ">tbody>tr:nth-child(2)>td:nth-child(' + (rows.length + 1) + ')').html(result.toFixed(2) + '%');$('#table_";

	/**
	 * Function_15_7 to create javascript for jqgrid Added in Release 6
	 */
	private static final String Function_15_7 = "} } function getColumnIndex(arr, columnName){var rowIndex = 0; for(var i in arr){if(arr[i].indexOf(columnName) == 0){rowIndex = i;++rowIndex;break;}}return rowIndex;}function getPagerDetails(tableId, pagerId){"
			+ "	$('#' + tableId).jqGrid( 'navGrid', '#' + pagerId, { add : false, edit : false,";
	// End Added in R6
	/**
	 * FUNCTION_16 to create javascript for jqgrid
	 */
	private static final String FUNCTION_16 = "				search : false,deltext:'Delete',"
			+ "				refresh : false},{},/*edit option*/"
			+ "			{},/*add option*/"
			+ "			{ beforeShowForm: function(form)  {"
			+ "if(lastsel != 'new_row'){"
			+ "	if(checkForZeroAndDelete != 'null'){"
			+ "		var tmp = checkForZeroAndDelete.split(',');"
			+ "		var flag = false;"
			+ "		$('#'+ subgrid_table_id+ '>tbody>#'+ lastsel+ '>td').each(function(i) {"
			+ "			if(getColName(this) == tmp[0]){"
			+ "				if(parseFloat(($(this).html()).replace('$','').replace(',','')) != 0){"
			+ "					flag = true;"
			+ "					if(parseFloat($(this).find('input').val()) != 0){"
			+ "						flag = true;"
			+ "					}else{"
			+ "						flag = false;"
			+ "					}"
			+ "				}"
			+ "			return false;"
			+ "			}"
			+ "		});"
			+ "		if(flag){"
			+ "			$('.ui-jqdialog-title').html('Error');"
			+ "		$('.delmsg').html(tmp[1]);"
			+ "			$('#dData').hide();"
			+ "			$('#eData').html('Ok<span class=\"ui-icon ui-icon-cancel\"></span>');"
			+ "			$('td#del_' + subgrid_table_id + ',td#' + subgrid_table_id + '_iledit').removeClass('ui-pg-button ui-corner-all ui-state-disabled');"
			+ "		}else{" + "			$('.ui-jqdialog-title').html('Delete');" + "			" + "			$('#dData').show();"
			+ "			$('#eData').html('Cancel<span class=\"ui-icon ui-icon-cancel\"></span>');" + "		}" + "	}" + "}"
			+ "						}," + "afterSubmit: function () { pageGreyOut(); if(checkForZeroAndDelete == \"null\" ){"
			+ "	var isEditable = false;" + "	$('#'+ subgrid_table_id+ '>tbody>#'+ lastsel+ '>td').each(function(i) {"
			+ "		if($(this).html().indexOf('input') == 1){" + "			isEditable = true;" + "		}	" + "	});"
			+ "	if(isEditable){" + "		clickOnGridArr = removeValue(clickOnGridArr,subgrid_table_id);" + "	}" + "}";

	/**
	 * FUNCTION_17_1 to create javascript for jqgrid Updated in Release 6
	 */
	private static final String FUNCTION_17_1 = "$(this).jqGrid('setGridParam', {datatype:'json'});"
	// Start Added in Release 6
			+ "return [true,''];" + "}" + "}/*delete option this will reload after delete operation*/);";
	/**
	 * FUNCTION_17_1_1 to create javascript for jqgrid Added in Release 6
	 */
	private static final String FUNCTION_17_1_1 = "$('#' + tableId).jqGrid('navButtonAdd', '#' + pagerId,"
			+ " {id:tableId+'_ilexport',caption: 'Export',buttonicon: 'export-jqGrid',title: 'Export All Rows To CSV',onClickButton: function(e) {"
			+ "$('#csvFileName').val('";
	/**
	 * FUNCTION_17_1_2 to create javascript for jqgrid Added in Release 6
	 */
	private static final String FUNCTION_17_1_2 = "');$('#csvRowData').val(JSONToCSVConvertor(parentTotal.rows, $('#table_";
	/**
	 * FUNCTION_17_1_3 to create javascript for jqgrid Added in Release 6
	 */
	private static final String FUNCTION_17_1_3 = "').jqGrid('getGridParam', 'colNames'))); document.getElementById(\"exportFileForm\").submit();},position:'last'}); ";
	/**
	 * FUNCTION_17_1_4 to create javascript for jqgrid Added in Release 6
	 */
	private static final String FUNCTION_17_1_4 = "$('#' + tableId).jqGrid('inlineNav','#' + pagerId,"
	// End Added in Release 6
			+ "			{edittext:'Edit',addtext:'Add',canceltext:'Cancel',savetext:'Save',";

	/**
	 * FUNCTION_17 to create javascript for jqgrid Updated in Release 6
	 */
	private static final String FUNCTION_17 = ",addParams : {" + "					position : 'afterSelected',"
			+ "					addRowParams : myEditOptions" + "				}," + "				addedrow : 'first',"
			+ "				editParams : myEditOptions" + "			});";

	/**
	 * FUNCTION_17_5 to create javascript for jqgrid Added in Release 6
	 */
	private static final String FUNCTION_17_5 = "}"
			+ "function getEditableFieldsFn(col){"
			+ "for ( var i = 0; i < col.length; i++) {"
			+ "var cm = $('#'+ subgrid_table_id).jqGrid('getColProp',col[i]);"
			+ "if (edtableRows != '') {"
			+ "	edtableRows = edtableRows+ ','+ col[i] + ':' + cm.editable;"
			+ "} else {"
			+ "	edtableRows = col[i]+ ':'+ cm.editable;"
			+ "}"
			+ "}"
			+ "getEditableFields = false;"
			+ "}"
			+ "function totalLastColumn(tblLen){"
			+ "colLen = $('#' + subgrid_table_id+ '>tbody>tr:nth-child(1)>td').length;"
			+ "var totRight = 0;"
			+ "for ( var count = 2; count <= tblLen; count++) {"
			+ "for ( var inCount = 1; inCount <= colLen; inCount++) {"
			+ "	var tmpTotalLastCol = totalLastCol.split(',');"
			+ "var tmpVal = $('#'+ subgrid_table_id+ '>tbody>tr:nth-child('+ (count)+ ')>td:nth-child('+ inCount + ')').html();"
			+ "	tmpVal = tmpVal.replace('($','-').replace(')','').replace('$','').replace(/,/g,'');"
			+ "	for ( var totCount = 0; totCount < tmpTotalLastCol.length; totCount++) {"
			+ "	var sign = '+';"
			+ "	sign = tmpTotalLastCol[totCount].charAt(0);"
			+ "	if (sign == '-') {"
			+ "		tmpTotalLastCol[totCount] = tmpTotalLastCol[totCount].replace('-', '');"
			+ "		if (tmpTotalLastCol[totCount] == getColName($('#'+ subgrid_table_id+ '>tbody>tr:nth-child('+ (count)+ ')>td:nth-child('+ inCount + ')'))) {"
			+ "			tmpVal = tmpVal.replace('-', '');"
			+ "			if (totRight == 0) {"
			+ "				totRight = new Big(tmpVal);"
			+ "			} else {"
			+ "				totRight = new Big(totRight).minus(new Big(tmpVal));"
			+ "			}"
			+ "		}"
			+ "	} else {"
			+ "		if (tmpTotalLastCol[totCount] == getColName($('#'+ subgrid_table_id+ '>tbody>tr:nth-child('+ (count)+ ')>td:nth-child('+ inCount + ')'))) {"
			+ "			if (totRight == 0) {"
			+ "				totRight = new Big(tmpVal);"
			+ "			} else {"
			+ "				totRight = new Big(totRight).plus(new Big(tmpVal));"
			+ "			}"
			+ "		}"
			+ "	}"
			+ "} "
			+ "}"
			+ "$('#' + subgrid_table_id+ '>tbody>tr:nth-child('+ (count)+ ')>td:nth-child('+ colLen + ')').html(formatCurrency(totRight));"
			+ "$('#' + subgrid_table_id+ '>tbody>tr:nth-child('+ (count) + ')>td:nth-child('+ colLen + ')').attr(\"title\",$('#' + subgrid_table_id+ '>tbody>tr:nth-child('+ (count) + ')>td:nth-child('+ colLen + ')').html());"
			+ "totRight = 0; " + "}" + "}"
			//Start:Added in R7
			+ "function negativeModification(rowId, tv, rawObject, cm, rdata) {if (tv.indexOf('(') != -1) {return ' style=\"background-color:mediumaquamarine;font-weight:bold\"';}}"
			//End: Added in R7
			+"function getColName(obj){"
			+ "	var tempId = $(obj).attr('aria-describedby');" + "	var n = tempId.lastIndexOf('_');"
			+ "	return tempId.substring(n + 1);	" + "}"
			+ "var setTooltipsOnColumnHeader = function (grid, iColumn, text) {"
			+ "var thd = jQuery('thead:first', grid[0].grid.hDiv)[0];"
			+ "jQuery('tr.ui-jqgrid-labels th:eq(' + iColumn + ')', thd).attr('title', text);" + "};";

	/**
	 * ADD_ROW_DATA to create javascript for jqgrid
	 */
	private static final String ADD_ROW_DATA = "var oldAddRowData = $.fn.jqGrid.addRowData;";

	/**
	 * EXTEND_ADD_ROW_DATA to create javascript for jqgrid
	 */
	private static final String EXTEND_ADD_ROW_DATA = "$.jgrid.extend({addRowData: function (rowid, rdata, pos, src) {"
			+ "var aa = oldAddRowData.call(this, rowid, rdata, 'first', src);"
			+ "	$('td#del_'+ subgrid_table_id).removeClass().addClass('ui-pg-button ui-corner-all ui-state-disabled');"
			+ "totalColumn = getSubGridIds(rowid);" + "var tempStringlen = totalColumn.split(',');"
			+ "var len = tempStringlen.length;" + "for ( var i = 0; i < len; i++) {"
			+ "$(this).jqGrid('setColProp',tempStringlen[i], {editable : true});" + "if(notEditableForAddRow != null){"
			+ "	var tmpnotEditableForAddRow = notEditableForAddRow.split(',');"
			+ "	for(var count = 0; count<tmpnotEditableForAddRow.length; count++){"
			+ "		if(tempStringlen[i] == tmpnotEditableForAddRow[count]){"
			+ "			$(this).jqGrid('setColProp',tempStringlen[i], {editable : false});" + "		}	" + "	}" + "}" + "}"
			+ "return aa;}});";

	/**
	 * OLD_INFO_DIALOG to create javascript for jqgrid
	 */
	@SuppressWarnings("unused")
	private static final String OLD_INFO_DIALOG = "	var oldInfoDialog = $.jgrid.info_dialog;" + "$.extend($.jgrid,{"
			+ "    info_dialog: function (caption, content, c_b, modalopt) {"
			+ "        if (modalopt && (modalopt.zIndex === null || modalopt.zIndex === undefined ||"
			+ "            (typeof modalopt.zIndex === \"number\" && modalopt.zIndex < 1234))) {"
			+ "	            modalopt.zIndex = 1234;" + "        }" + "if(content.indexOf('Field is required') !== -1){"
			+ "	content = \"Specify a value in \"+getHeaderName(headerName,content);"
			+ "}else if(content.indexOf(checkForTotalValue[2]) !== -1){" + "	content =  checkForTotalValue[2];"
			+ "}else if(content.indexOf('allowOnlyPositiveValue') !== -1) {" + "if(msgKey != ''){"
			+ "	content = msgKey;" + "}else{" + "	content = \"Allow only positive values !!\";" + "}"
			+ "	if(checkForTotalValueData < 0){" + "		content = checkForTotalValue[2]; " + "	}"
			+ "}else if(content.indexOf('Please, enter valid integer value') >= -1){"
			+ "	content = getHeaderName(headerName,content) +\" Field Can have only integer value\";"
			+ "}else if(content.indexOf('+') >= 2) {" + "if(msgKey != ''){" + "	content = msgKey;" + "}else{"
			+ "	content = \"Allow only negative values !!\";" + "}" + "}"
			+ "else if (content.indexOf('containDuplicateValues') !== -1){"
			+ "	content = 'Duplicate Values are not allowed';" + "}"
			+ " else if (content.indexOf('containDuplicateValuesForOther') !== -1){"
			+ "		content = 'Duplicate Values are not allowed';" + "	}"
			+ "else if (content.indexOf('IsNotAPercentageFormat') !== -1) {"
			+ "content = 'Percentage Format is Invalid';" + "} else if (content.indexOf('%') !== -1) {"
			+ "content = 'Maximum acceptable values is 100';"
			+ "} else if(content.indexOf('valuesuobcinvalid') !== -1){" + "content = 'UoA*-BC*-OC* is Invalid';}"
			+ "return oldInfoDialog.call (this, caption, content, c_b, modalopt);" + "   }" + "});";

	/**
	 * JSON_READER_SUB_GRID_1 to create javascript for jqgrid
	 */
	private static final String JSON_READER_SUB_GRID_1 = ",jsonReader: {page : function(obj) { "
			+ "if(obj.page > 1){"
			+ "gridPage = obj.page;"
			+ "}"
			+ "return 1; },total: 'total', root: 'rows',records: 'records',"
			+ "repeatitems: false, userdata:function(obj){parentTotal = obj;if($.trim(obj.error) != ''){$('#serverError";

	/**
	 * JSON_READER_SUB_GRID_2 to create javascript for jqgrid
	 */
	private static final String JSON_READER_SUB_GRID_2 = "').show();$('#serverError";

	/**
	 * JSON_READER_SUB_GRID_3 to create javascript for jqgrid
	 */
	private static final String JSON_READER_SUB_GRID_3 = "').html(obj.error);}else{$('#serverError";

	/**
	 * JSON_READER_SUB_GRID_4 to create javascript for jqgrid
	 */
	private static final String JSON_READER_SUB_GRID_4 = "').hide();}}},";

	/**
	 * JSON_READER_GRID_1 to create javascript for jqgrid
	 */
	private static final String JSON_READER_GRID_1 = ",jsonReader: {page: 'page',total: 'total', root: 'rows',records: 'records',"
			+ "repeatitems: false, userdata:function(obj){if($.trim(obj.error) != ''){$('#serverError";

	/**
	 * JSON_READER_GRID_2 to create javascript for jqgrid
	 */
	private static final String JSON_READER_GRID_2 = "').show();$('#serverError";

	/**
	 * JSON_READER_GRID_3 to create javascript for jqgrid
	 */
	private static final String JSON_READER_GRID_3 = "').html(obj.error);}else{$('#serverError";

	/**
	 * JSON_READER_GRID_4 to create javascript for jqgrid
	 */
	private static final String JSON_READER_GRID_4 = "').hide();}}},";

	/**
	 * STR_JS_7 to create javascript for jqgrid
	 */
	private static final String STR_JS_7 = "autoencode: true,ignoreCase: true";

	/**
	 * STR_JS_7_1 to create javascript for jqgrid
	 */
	private static final String STR_JS_7_1 = "subGrid : ";

	/**
	 * STR_JS_8: Break down into two constants for Release 3.4.0, #5681
	 * STR_JS_8_1 to create javascript for jqgrid
	 */
	private static final String STR_JS_8_1 = ",onSelectRow : function(rowid){$('#'+rowid).removeClass('ui-state-highlight');},subGridOptions: {expandOnLoad: ";

	/**
	 * STR_JS_8: Break down into two constants for Release 3.4.0, #5681
	 * STR_JS_8_2 to create javascript for jqgrid
	 */
	private static final String STR_JS_8_2 = ", reloadOnExpand : false},subGridRowExpanded: function(subgrid_id, row_id) {";

	/**
	 * STR_JS_9 to create javascript for jqgrid
	 */
	private static final String STR_JS_9 = "subgrid_table_id = subgrid_id+'_t'; pager_id = 'p_'+subgrid_table_id;"
			+ "$('#'+subgrid_id).html(\"<table id='\"+subgrid_table_id+\"' class='scroll'></table><div id='\"+pager_id+\"'"
			+ " style='display:block;' class='scroll'></div>\");" + "jQuery('#'+subgrid_table_id).jqGrid({url: '";

	/**
	 * STR_JS_10 to create javascript for jqgrid
	 */
	private static final String STR_JS_10 = "autoencode: true,ignoreCase: true, height: '100%',autowidth:";

	/**
	 * STR_JS_11 to create javascript for jqgrid
	 */
	private static final String STR_JS_11 = ",pager: '#'+pager_id," + "cellEdit: false,cellsubmit: 'remote',cellurl:'";

	/**
	 * LOAD_COMPLETE_GRID to create javascript for jqgrid Updated in Release 6
	 */
	private static final String LOAD_COMPLETE_GRID = ",loadComplete : function() {"
			+ "	$('td#del_'+ subgrid_table_id + ',td#'+ subgrid_table_id + '_iledit').removeClass().addClass('ui-pg-button ui-corner-all ui-state-disabled');"
			+ "	var tblLen = $('#' + subgrid_table_id + '>tbody>tr').length;"
			+ "	if (totalLastCol != '') {"
			+ "		totalLastColumn(tblLen);"
			+ "	}"
			+ "	var rowid = $('#'+ subgrid_table_id+ '>tbody>tr:nth-child(2)').attr('id');"
			+ "	totalColumn = getSubGridIds(rowid);"
			+ "	var tempStringlen = totalColumn.split(',');"
			+ "	/*To get the editable field in the beginning*/"
			+ "	if (getEditableFields) {"
			+ "		getEditableFieldsFn(tempStringlen);"
			+ "	}"
			+ "	/*Change negative fields for currency*/"
			+ "	var tbLen = $('#'"
			+ "			+ subgrid_table_id"
			+ "			+ '>tbody>tr').length;"
			+ "	for ( var count = 1; count <= tbLen; count++) {"
			+ "		for ( var inCount = 1; inCount <= tempStringlen.length; inCount++) {"
			+ "			var tmpVal = $('#'+ subgrid_table_id+ '>tbody>tr:nth-child('+ count+ ')>td:nth-child('+ inCount+ ')').html();"
			+ "			if (tmpVal != null) {"
			+ "				if (tmpVal.indexOf('$-') == 0) {"
			+ "					$('#'+ subgrid_table_id+ '>tbody>tr:nth-child('	+ count+ ')>td:nth-child('+ inCount+ ')').html(formatCurrency(tmpVal));"
			+ "				}" + "			}" + "		}" + "	} ";

	/**
	 * LOAD_COMPLETE_SUB_GRID_1 to create javascript for jqgrid Updated in
	 * Release 6
	 */
	private static final String LOAD_COMPLETE_SUB_GRID_1 = "loadComplete: function () {"
			+ "if(dropDownData != 'null'){"
			+ "$('#'+subgrid_table_id).setColProp('empPosition', { editoptions: { value: dropDownData,dataInit: function(elem) {$(elem).width(subGridRowNumber==null?230:230-subGridRowNumber);}} });"
			+ "}var columnNames = $('#table_";

	/**
	 * LOAD_COMPLETE_SUB_GRID_2 to create javascript for jqgrid
	 */
	private static final String LOAD_COMPLETE_SUB_GRID_2 = "').jqGrid('getGridParam','colNames');var colModel = $('#table_";

	/**
	 * LOAD_COMPLETE_SUB_GRID_3 to create javascript for jqgrid
	 */
	private static final String LOAD_COMPLETE_SUB_GRID_3 = "').jqGrid('getGridParam','colModel');"
			+ "for(var headCount=0; headCount<columnNames.length ; headCount++){"
			+ "	if (columnNames[headCount] != '') {" + " if (headerName != null) {"
			+ "		var tmp = columnNames[headCount] + ':'+ colModel[headCount].name;"
			+ " 	headerData = headerData + '|' + tmp;" + "		if(headerNameDuplicateFound(tmp)){"
			+ "			headerName = headerName + '|' + tmp;" + "		}" + "		} else {"
			+ "		headerName = columnNames[headCount] + ':' + colModel[headCount].name;"
			+ "     headerData = headerName;" + " 	}" + "	}" + "}" + "if(applyStyle){$('#table_";

	/**
	 * LOAD_COMPLETE_SUB_GRID_4 to create javascript for jqgrid
	 */
	private static final String LOAD_COMPLETE_SUB_GRID_4 = ">tbody>tr:nth-child(2)>td').each(function(i) { "
			+ "	if($(this).html().indexOf('$0.00') == 0){"
			+ "		$(this).attr('style','font-weight:bold;text-align:right');"
			+ "	}else if($(this).html().indexOf('0') == 0){"
			+ "$(this).attr('style','font-weight:bold;text-align:center');"
			+ "}else{$(this).attr('style','font-weight:bold');"
			+ "	}"
			+ "});applyStyle = false;}$('td#del_'+ subgrid_table_id+',td#'+ subgrid_table_id+ '_iledit').removeClass()."
			+ "addClass('ui-pg-button ui-corner-all ui-state-disabled');"
			+ "var tempStringlen; var rowid = $('#'+ subgrid_table_id+ '>tbody>tr:nth-child(2)').attr('id');"
			+ "totalColumn = getSubGridIds(rowid); var tempStringlen = totalColumn.split(',');"
			+ "/*To get the editable field in the beginning*/" + "if(getEditableFields){"
			+ "	for ( var i = 0; i < tempStringlen.length; i++) {"
			+ "		var cm = $('#'+ subgrid_table_id).jqGrid('getColProp',tempStringlen[i]);" + "		if(edtableRows!= ''){"
			+ "			edtableRows = edtableRows + ',' + tempStringlen[i] + ':' + cm.editable;" + "		}else{"
			+ "			edtableRows = tempStringlen[i] + ':' + cm.editable;" + "		}" + "if(columnNames[i] != ''){"
			+ "/*	if(headerName != null){"
			+ "		headerName = headerName + '|'+ columnNames[i] + ':' + tempStringlen[i-1];" + "	}else{"
			+ "		headerName = columnNames[i] + ':' + tempStringlen[i-1];	" + "	}*/" + "}" + "	}"
			+ "	getEditableFields = false;" + "}" + "if(parentTotal.rows.length > 0 ){" + "	updateParentGrid();" + "}"
			+ "else{getEditableFields = true;edtableRows = '';var formatType='";

	/**
	 * LOAD_COMPLETE_SUB_GRID_5 to create javascript for jqgrid
	 */
	private static final String LOAD_COMPLETE_SUB_GRID_5 = "';formatType = formatType.split(',');$('#table_";

	/**
	 * LOAD_COMPLETE_SUB_GRID_6 to create javascript for jqgrid Updated in
	 * Release 6
	 */
	private static final String LOAD_COMPLETE_SUB_GRID_6 = ">tbody>tr:nth-child(2)>td').each(function(i) {"
			+ "if(i > 0)"
			+ "{"
			+ "var tmpVal = $ .trim($(this).html());"
			+ "if(tmpVal == '' || tmpVal == '&nbsp;')"
			+ "{"
			+ "	$(this).attr('style','background:lightgrey;');"
			+ "}"
			+ "else"
			+ "{"
			+ "	if(formatType[i] == 'numberTemplate')"
			+ "	{"
			+ "		$(this).html('0.00'); $(this).attr('title','0.00');"
			+ "	}"
			+ "	else if(formatType[i] == 'percentageTemplate')"
			+ "	{"
			+ "		$(this).html('0.00%'); $(this).attr('title','0.00%');"
			+ "	}"
			+ "	else if(formatType[i] == 'integerTemplate')"
			+ "	{"
			+ "		$(this).html('0'); $(this).attr('title','0');"
			+ "	}"
			+ "	else if(formatType[i] == 'currencyTemplate')"
			+ "	{"
			+ "$(this).attr('style','font-weight:bold;text-align:right');"
			+ "		$(this).html('$0.00'); $(this).attr('title','$0.00');"
			+ "	}"
			//Start: Added in R7
			+ "else if (formatType[i] == 'negativeModificationTemplate')" 
			+" {"
			+ "$(this).attr('style', 'font-weight:bold;text-align:right');"
			+ "$(this).html('$0.00');"
			+ "$(this).attr('title', '$0.00');"
            + " }"
			//End: Added in R7
			+ "}"
			+ "}"
			+ "});"
			+ 
			"}"
			+ "/*Change negative fields for currency*/"
			+ "var tbLen = $('#' + subgrid_table_id + '>tbody>tr').length;"
			+ "for(var count=1; count<=tbLen; count++){"
			+ "for(var inCount=1; inCount<=tempStringlen.length;inCount++){"
			+ "var tmpVal = $('#' + subgrid_table_id+ '>tbody>tr:nth-child('+ count + ')>td:nth-child('"
			+ "+ inCount + ')').html(); if(tmpVal != null){"
			+ "if(tmpVal.indexOf('$-') == 0){"
			+ "$('#' + subgrid_table_id+ '>tbody>tr:nth-child('+ count + ')>td:nth-child('"
			+ "+ inCount + ')').html(formatCurrency(tmpVal));"
			+ "}"
			+ "}"
			+ "}"
			+ "}"
			+ "if(totalLastCol != ''){"
			+ "	var n = totalColumn.lastIndexOf(',');"
			+ "	if(notEditableForAddRow != 'null'){"
			+ "		notEditableForAddRow = notEditableForAddRow + ',' + totalColumn.substring(n + 1);"
			+ "	}else{"
			+ "		notEditableForAddRow = totalColumn.substring(n + 1);"
			+ "	}"
			+ "}"
			+ "	if(lastRowEdit == 'true'){"
			+ "		lastRowEdit = getColName($('#'+subgrid_table_id+'>tbody>tr:nth-child('+jQuery(\"tr\", \"#\"+subgrid_table_id).length+')>td:nth-child(1)'))"
			+ "		+','+$('#'+subgrid_table_id+'>tbody>tr:nth-child('+jQuery(\"tr\", \"#\"+subgrid_table_id).length+')').attr('id');	"
			+ "	}"
			+ "if(gridPage > 1){"
			+ "$('#p_'+ subgrid_table_id+ '_center>table>tbody>tr>td:eq(3)>.ui-pg-input').val(gridPage);"
			+ "$('#p_'+ subgrid_table_id+ '_center>table>tbody>tr>td:eq(3)>.ui-pg-input').focus();"
			+ "var e = jQuery.Event('keypress');"
			+ "e.which = 13; /*choose the one you want*/"
			+ "e.keyCode = 13;"
			+ "setTimeout("
			+ "function() {"
			+ "	$('#p_'+ subgrid_table_id+ '_center>table>tbody>tr>td:eq(3)>.ui-pg-input').trigger(e);"
			+ "},100);"
			+ "gridPage=1;"
			+ "}"
			+ " if($('#p_'+ subgrid_table_id+ '_center>table>tbody>tr>td:eq(3)>span').html() == 0){"
			+ "	setTimeout(function() {"
			+ "		$('#p_' + subgrid_table_id + '_center>table>tbody>tr>td:eq(5)').addClass('ui-pg-button ui-corner-all ui-state-disabled');"
			+ "		$('#p_' + subgrid_table_id + '_center>table>tbody>tr>td:eq(6)').addClass('ui-pg-button ui-corner-all ui-state-disabled');"
			+ "	},100);" + "}";

	/**
	 * ON_DBL_CLICK_ROW to create javascript for jqgrid.
	 * 
	 * Updated in Release 6.
	 */
	private static final String ON_DBL_CLICK_ROW = "ondblClickRow: function(id){ editPage(); currentId = id; editButton =false;"
			+ "if(!isArrayContainsDuplicateValue(clickOnGridArr,subgrid_table_id)){"
			+ "	clickOnGridArr.push(subgrid_table_id);"
			+ "}"
			+ "/* For edit row change make the column editable/non-editable*/"
			+ "if(id !== 'new_row'){"
			+ "	var editTemp = edtableRows.split(',');"
			+ "	for ( var i = 0; i < editTemp.length; i++) {"
			+ "		var tempCol = editTemp[i].split(':');"
			+ "		if(tempCol[1] == 'true'){"
			+ "			$(this).jqGrid('setColProp', tempCol[0],{ editable : true });"
			+ "		}else{"
			+ "			$(this).jqGrid('setColProp', tempCol[0],{ editable : false });"
			+ "		}"
			+ "	}"
			+ "}"
			+ "if(lastRowEdit != 'null'){"
			+ "	var tmp = lastRowEdit.split(',');"
			+ "	if(id == tmp[1]){"
			+ "		$(this).jqGrid('setColProp',tmp[0], {editable : true});"
			+ "	}else{$(this).jqGrid('setColProp',tmp[0], {editable : false});}"
			+ "}"
			+ "/*Replace bracket with -ve value */"
			+ "currentRowInfo = null;"
			+ "$('#' + subgrid_table_id+ '>tbody>#'	+ id + '>td').each(function(i) {"
			+ "	var editTemp = edtableRows.split(',');"
			+ "	for ( var i = 0; i < editTemp.length; i++) {"
			+ "		var tempCol = editTemp[i].split(':');"
			+ "		if(tempCol[0] == getColName(this)){"
			+ "			if(tempCol[1] == 'true'){"
			+ "				if ($(this).html().indexOf('($') == 0) {"
			+ "					var oldVal = $(this).html().replace(')','');"
			+ "					var newVal = oldVal.replace('($','-');"
			+ "					$(this).html(newVal);"
			+ "				}else if ($(this).html()[$(this).html().length - 1] === '%') {"
			+ "					$(this).html($(this).html().replace('%',''));"
			+ "				}	"
			+ "			}"
			+ "		}"
			+ "	}"
			+ "getCurrEditRowVal(this);"
			+ "});"
			+ "/*Replace -ve value with bracket*/"
			+ "$('#' +subgrid_table_id+'>tbody>#' + lastsel + '>td').each(function(i) {"
			+ "if($(this).html().indexOf('$-')==0){"
			+ "var oldVal = $(this).html().replace('$-','($');"
			+ "var newVal = oldVal + ')';$(this).html(newVal);}});	"
			+ "if(id && id!==lastsel){"
			+ "/*jQuery('#'+subgrid_table_id).restoreRow(lastsel);*/"
			+ "jQuery('#'+subgrid_table_id).jqGrid('editRow',id,  myEditOptions); "
			+ "lastsel=id; "
			+ "}else{/*jQuery('#'+subgrid_table_id).restoreRow(lastsel);*/"
			+ "jQuery('#'+subgrid_table_id).jqGrid('editRow',id,  myEditOptions); "
			+ "}"
			+ "setTimeout(function(){$('td#del_'+subgrid_table_id+',td#'+subgrid_table_id+'_ilsave,td#'+subgrid_table_id+'_ilcancel').removeClass('ui-pg-button ui-corner-all ui-state-disabled');"
			+ "   $('td#del_'+ subgrid_table_id+ ',td#'+ subgrid_table_id+ '_ilsave,td#'+ subgrid_table_id+ '_ilcancel').removeClass('ui-pg-button ui-corner-all ui-state-disabled');"
			+ "$('td#'+ subgrid_table_id+ '_iledit'+ ',td#'+ subgrid_table_id+ '_iladd').removeClass().addClass('ui-pg-button ui-corner-all ui-state-disabled');"
			+ "$('td#del_'+ subgrid_table_id).removeClass().addClass('ui-pg-button ui-corner-all ui-state-disabled');},100);}";

	/**
	 * ON_SELECT_ROW_GRID to create javascript for jqgrid
	 */
	private static final String ON_SELECT_ROW_GRID = ",onSelectRow : function(rowid) {editButton = true;"
			+ "if(rowid != 'new_row' && $('#'+ rowid).attr('aria-selected') == 'true'){"
			+ "setTimeout(function(){$('td#del_'+ subgrid_table_id+ ',td#'+ subgrid_table_id	+ '_iledit,td#'+ subgrid_table_id	+ '_iladd').removeClass('ui-pg-button ui-corner-all ui-state-disabled');},100);"
			+ "if($('#'+ rowid).attr('editable')!= 'undefined' && $('#'+ rowid).attr('editable') == '1' ){"
			+ "setTimeout(function(){$('td#del_'+ subgrid_table_id+ ',td#'+ subgrid_table_id	+ '_iledit,td#'+ subgrid_table_id	+ '_iladd').removeClass().addClass('ui-pg-button ui-corner-all ui-state-disabled');},100);"
			+ "}" + "}" + "},";

	/**
	 * ON_SELECT_ROW_SUB_GRID to create javascript for jqgrid.
	 * 
	 * Updated in Release 6
	 */
	private static final String ON_SELECT_ROW_SUB_GRID = ",onSelectRow : function(rowid) {"
			+ "if(rowid != 'new_row' && $('#'+ rowid).attr('aria-selected') == 'true'){"
			+ "setTimeout(function(){$('td#del_'+ subgrid_table_id+ ',td#'+ subgrid_table_id+ '_iledit').removeClass('ui-pg-button ui-corner-all ui-state-disabled');},100);"
			+ "if($('#'+ rowid).attr('editable')!= 'undefined' && $('#'+ rowid).attr('editable') == '1' ){"
			+ "setTimeout(function(){	$('td#del_'+ subgrid_table_id+ ',td#'+ subgrid_table_id+ '_iledit').removeClass().addClass('ui-pg-button ui-corner-all ui-state-disabled');},100);"
			+ "}"
			+ "}"
			+ "editButton = true;"
			+ "if (checkForTotalValue != 'null') {"
			+ "	checkForTotalValueData = 0;"
			+ "	checkForTotalValueData = $('#'+ subgrid_table_id).jqGrid('getCell',rowid,checkForTotalValue[0]);"
			+ "}"
			+ "lastsel = rowid;"
			+ "if(isNewRecordDelete != 'null' && isNewRecordDelete == 'true'){if(rowid.indexOf('_newrecord') !== -1){"
			+ "/*enable delete button*/setTimeout(function(){$('td#del_'+ subgrid_table_id).removeClass('ui-pg-button ui-corner-all ui-state-disabled');},100);"
			+ "if($('#'+ rowid).attr('aria-selected') == 'true' && $('#'+ rowid).attr('editable')!= 'undefined' && $('#'+ rowid).attr('editable') == '1'){"
			+ "	setTimeout(function(){$('td#del_'+ subgrid_table_id +',td#'+ subgrid_table_id+ '_iledit').removeClass().addClass('ui-pg-button ui-corner-all ui-state-disabled');},100);"
			+ "}"
			+ "}else{/*disable delete button*/"
			+ "setTimeout(function(){$('td#del_'+ subgrid_table_id).removeClass().addClass('ui-pg-button ui-corner-all ui-state-disabled');},100);}}"
			+ "if(lastRowEdit != 'null'){ 	var tmp = lastRowEdit.split(','); 	if(rowid == tmp[1]){"
			+ "		$(this).jqGrid('setColProp',tmp[0], {editable : true});"
			+ "	}else{$(this).jqGrid('setColProp',tmp[0], {editable : false});} }"
			// Start : Added in Release 6
			+ "if(exportFileName != 'null' && rowid != 'new_row' && $('#'+rowid).attr('editable') != '1'){"
			+ "	$('td#'+subgrid_table_id+'_ilexport').removeClass().addClass('ui-pg-button ui-corner-all');}"
			// End : Added in Release 6
			+ "}";

	/**
	 * OLD_EDIT_ROW to create javascript for jqgrid
	 */
	@SuppressWarnings("unused")
	private static final String OLD_EDIT_ROW = "var oldEditRow = $.fn.jqGrid.editRow;" + "$.jgrid.extend({"
			+ "	editRow: function (iRow,iCol, ed){" + "			if(iRow.substring(iRow.lastIndexOf('_') + 1) == 'newrow'){"
			+ "				totalColumn = getSubGridIds(iRow);" + "				var tempStringlen = totalColumn.split(',');"
			+ "				var len = tempStringlen.length;" + "				for ( var i = 0; i < len; i++) {"
			+ "					$(this).jqGrid('setColProp',tempStringlen[i], {editable : true});"
			+ "					if (notEditableForAddRow != null) {"
			+ "						var tmpnotEditableForAddRow = notEditableForAddRow.split(',');"
			+ "						for ( var count = 0; count < tmpnotEditableForAddRow.length; count++) {"
			+ "							if (tempStringlen[i] == tmpnotEditableForAddRow[count]) {"
			+ "								$(this).jqGrid('setColProp',tempStringlen[i],{editable : false});" + "							}" + "						}"
			+ "					}" + "				}" + "   		}" + "if (iRow.indexOf('_newrecord') !== -1){"
			+ "	var tempStringlen = totalColumn.split(',');"
			+ "		$(this).jqGrid('setColProp',tempStringlen[0], {editable : true});" + "	}"
			+ "       return oldEditRow.call (this, iRow, iCol, ed); " + "   }" + "});";

	/**
	 * LOAD_ERROR_1 to create javascript for jqgrid
	 */
	private static final String LOAD_ERROR_1 = "loadError: function(xhr,st,err) {$('#loadError";

	/**
	 * LOAD_ERROR_2 to create javascript for jqgrid
	 */
	private static final String LOAD_ERROR_2 = "').html('Error : Type: '+st+'; Response: '+ xhr.status + ' '+xhr.statusText);},";

	/**
	 * STR_JS_12 to create javascript for jqgrid
	 */
	private static final String STR_JS_12 = "});getPagerDetails(subgrid_table_id, pager_id); $('.ui-jqgrid-hdiv', '#gbox_'+subgrid_table_id).hide(); $('.ui-separator').hide();";

	/**
	 * gridUrl to create javascript for jqgrid
	 */
	private String gridUrl = getGridUrl();
	/**
	 * editUrl to create javascript for jqgrid
	 */
	private String editUrl = getEditUrl();
	/**
	 * subGridUrl to create javascript for jqgrid
	 */
	private String subGridUrl = getSubGridUrl();
	/**
	 * cellUrl to create javascript for jqgrid
	 */
	private String cellUrl = getCellUrl();
	/**
	 * dataType to create javascript for jqgrid
	 */
	private String dataType = getDataType();
	/**
	 * methodType to create javascript for jqgrid
	 */
	private String methodType = getMethodType();
	/**
	 * Added in Release 6 - To create rownumber for PS Enhancement
	 */
	private String subGridRowNumbers;
	/**
	 * gridColNames to create javascript for jqgrid
	 */
	private String gridColNames = getGridColNames();
	/**
	 * subGridColProp to create javascript for jqgrid
	 */
	private String subGridColProp = getSubGridColProp();
	/**
	 * gridColProp to create javascript for jqgrid
	 */
	private String gridColProp = getGridColProp();
	/**
	 * columnTotalName to create javascript for jqgrid
	 */
	private String columnTotalName = getColumnTotalName();
	/**
	 * isSubGrid to create javascript for jqgrid
	 */
	private String isSubGrid = getIsSubGrid();
	/**
	 * operations to create javascript for jqgrid
	 */
	private String operations = getOperations();
	/**
	 * rowsPerPage to create javascript for jqgrid
	 */
	private String rowsPerPage = getRowsPerPage();
	/**
	 * isPagination to create javascript for jqgrid
	 */
	private String isPagination = getIsPagination();
	/**
	 * isReadOnly to create javascript for jqgrid
	 */
	private String isReadOnly = getIsReadOnly();
	/**
	 * nonEditColumnName to create javascript for jqgrid
	 */
	private String nonEditColumnName = getNonEditColumnName();
	/**
	 * modificationType to create javascript for jqgrid
	 */
	private String modificationType = getModificationType();
	/**
	 * lastRowEdit to create javascript for jqgrid
	 */
	private String lastRowEdit = getLastRowEdit();
	/**
	 * checkForTotalValue to create javascript for jqgrid
	 */
	private String checkForTotalValue = getCheckForTotalValue();
	/**
	 * checkForZeroAndDelete to create javascript for jqgrid
	 */
	private String checkForZeroAndDelete = getCheckForZeroAndDelete();
	/**
	 * notAllowDuplicateColumn to create javascript for jqgrid
	 */
	private String notAllowDuplicateColumn = getNotAllowDuplicateColumn();
	/**
	 * autoWidth to create javascript for jqgrid
	 */
	private String autoWidth = getAutoWidth();
	/**
	 * isCOAScreen to create javascript for jqgrid
	 */
	private String isCOAScreen = getIsCOAScreen();
	/**
	 * isNewRecordDelete to create javascript for jqgrid
	 */
	private String isNewRecordDelete = getIsNewRecordDelete();
	/**
	 * callbackFunction to create javascript for jqgrid
	 */
	private String callbackFunction = getCallbackFunction();

	/**
	 * negativeCurrency to create javascript for jqgrid
	 */
	private String negativeCurrency = getNegativeCurrency();

	/**
	 * positiveCurrency to create javascript for jqgrid
	 */
	private String positiveCurrency = getPositiveCurrency();

	/**
	 * setDropDownData to create javascript for jqgrid
	 */
	private String dropDownData = getDropDownData();

	/**
	 * Added for Release 3.4.0, #5681 - JQ Grid By Default Expanded
	 * isExpandOnLoad to create javascript for jqgrid
	 */
	private String isExpandOnLoad = getIsExpandOnLoad();

	/**
	 * Added in Release 6, exportFileName to create javascript for jqgrid,
	 * Export functionality for PS Enhancement
	 */
	private String exportFileName;

	/**
	 * Added in Release 6, callbackFnAfterLoadGrid to create javascript for
	 * jqgrid. To call javascript function after grid fully load.
	 */
	private String callbackFnAfterLoadGrid;

	/**
	 * @return the subGridRowNumbers
	 */
	public String getSubGridRowNumbers()
	{
		return subGridRowNumbers;
	}

	/**
	 * @param subGridRowNumbers the subGridRowNumbers to set
	 */
	public void setSubGridRowNumbers(String subGridRowNumbers)
	{
		this.subGridRowNumbers = subGridRowNumbers;
	}

	/**
	 * @return the callbackFnAfterLoadGrid
	 */
	public String getCallbackFnAfterLoadGrid()
	{
		return callbackFnAfterLoadGrid;
	}

	/**
	 * @param callbackFnAfterLoadGrid the callbackFnAfterLoadGrid to set
	 */
	public void setCallbackFnAfterLoadGrid(String callbackFnAfterLoadGrid)
	{
		this.callbackFnAfterLoadGrid = callbackFnAfterLoadGrid;
	}

	/**
	 * @return the exportFileName
	 */
	public String getExportFileName()
	{
		return exportFileName;
	}

	/**
	 * @param exportFileName the exportFileName to set
	 */
	public void setExportFileName(String exportFileName)
	{
		this.exportFileName = exportFileName;
	}

	/**
	 * serialVersionUID object
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * loColModel to create javascript for jqgrid
	 */
	StringBuffer loColModel = new StringBuffer();
	/**
	 * lsTempColModel to create javascript for jqgrid
	 */
	String lsTempColModel = "";
	/**
	 * lsPager to create javascript for jqgrid
	 */
	String lsPager = "";
	/**
	 * lsPagination to create javascript for jqgrid
	 */
	String lsPagination = "";
	/**
	 * lsTableName to create javascript for jqgrid
	 */
	String lsTableName = "";
	/**
	 * loGridFnData to create javascript for jqgrid
	 */
	StringBuffer loGridFnData = new StringBuffer();
	/**
	 * lsSubGridFnData to create javascript for jqgrid
	 */
	String lsSubGridFnData = "";
	/**
	 * lsNavgrid to create javascript for jqgrid
	 */
	String lsNavgrid = "";
	/**
	 * lsEditRule to create javascript for jqgrid
	 */
	String lsEditRule = "";
	/**
	 * lsMsgKey to create javascript for jqgrid
	 */
	String lsMsgKey = "";
	/**
	 * lsCallBackFn to create javascript for jqgrid
	 */
	String lsCallBackFn = "";
	/**
	 * lsTemp1 to create javascript for jqgrid
	 */
	String lsTemp1 = "";
	/**
	 * lsCurrencyInfo to create javascript for jqgrid
	 */
	StringBuffer loCurrencyInfo = new StringBuffer();

	/**
	 * lsCallBackFnAfteLoadGrid to create javascript for jqgrid Added in Release
	 * 6
	 */
	String lsCallBackFnAfteLoadGrid = "";

	@Override
	/** Updated in Release 6 */
	public int doStartTag() throws JspException
	{
		checkInitialVariable();
		JspWriter loWriterObj = pageContext.getOut();
		try
		{
			getColModel();
			getOperation();
			loGridFnData.append("colModel: [{");
			loGridFnData.append(loColModel);
			loGridFnData.append("}],");
			lsTemp1 = loGridFnData.toString();
			loGridFnData.delete(0, loGridFnData.length());
			loGridFnData.append(lsTemp1.replace("},{", ",sortable:false,resizable:false},{"));
			lsTemp1 = loGridFnData.toString();
			loGridFnData = new StringBuffer();
			loGridFnData.append(lsTemp1.replaceAll("\\s", "").replace("}],", ",sortable:false,resizable:false}],"));
			if (isSubGrid.equalsIgnoreCase(HHSConstants.FALSE))
			{
				lsTableName = "='table_" + id + "'";
				lsPager = ",loadonce: false,pager : '#pager_" + id + "'";
				loGridFnData.append("rowNum:");
				loGridFnData.append(rowsPerPage);
				loGridFnData.append(LOAD_COMPLETE_GRID);
				// Start: Updated in Release 6
				loGridFnData.append(lsCallBackFnAfteLoadGrid + "removePageGreyOut();},");
				// End: Updated in Release 6
				loGridFnData.append(ON_DBL_CLICK_ROW);
				loGridFnData.append(ON_SELECT_ROW_GRID);
				lsTemp1 = loGridFnData.toString();
				loGridFnData = new StringBuffer();
				loGridFnData.append(lsTemp1.replace("{,sortable:false,resizable:false},", ""));
			}
			else
			{
				lsTemp1 = loGridFnData.toString();
				loGridFnData = new StringBuffer();
				loGridFnData.append(lsTemp1.replace(",template:integerTemplate", "")
						.replace(",template:numberTemplate", "").replace(",template:currencyTemplate", "")
						.replace(",template:negativeModificationTemplate", "")
						.replace(",template:integerCommaFormatTemplate", "") // R 7.12.0
						.replace(",template:percentageTemplate", "").replace("{,sortable:false,resizable:false},", ""));
				loGridFnData.append("loadComplete:function(){ $('#table_");
				loGridFnData.append(id);
				loGridFnData
						.append(" tbody tr:nth-child(2) td:nth-child(1) a span').trigger(\"click\");if(clickOnce){$(\"#table_");
				loGridFnData.append(id);
				loGridFnData
						.append(" tbody tr:nth-child(2) td:nth-child(1) a span\").trigger(\"click\");clickOnce = false;}if(notAllowDuplicate != 'null'){setTooltipsOnColumnHeader($(\"#table_");
				loGridFnData.append(id);
				loGridFnData
						.append("\"), 1, \"UoA = Unit of Appropriation; BC = Budget Code; OC = Object Code\");setTooltipsOnColumnHeader($(\"#table_");
				loGridFnData.append(id);
				loGridFnData.append("\"), 2, \"SubOC = Sub Object Code\"); setTooltipsOnColumnHeader($(\"#table_");
				loGridFnData.append(id).append("\"), 3, \"RC = Reporting Category\");}");
				//Start: Added in R7
				loGridFnData.append("var columnNames = $('#table_").append(id).append("\').jqGrid('getGridParam', 'colNames');");
				loGridFnData.append("for(var i=1; i<=columnNames.length; i++){modFlag.push('0');}");
				loGridFnData.append("},");
				//End: Added in R7
				lsTemp1 = loColModel.toString();
				loColModel.delete(0, loColModel.length());
				loColModel.append("{");
				loColModel.append(lsTemp1);
				// Added a tweak Release 3.4.0, #5681
				// Updated in Release 6
				lsSubGridFnData = STR_JS_8_1 + isExpandOnLoad + STR_JS_8_2 + STR_JS_9 + subGridUrl
						+ "',loadonce: true,datatype: '" + dataType + "',mtype: '" + methodType + "'"
						+ JSON_READER_SUB_GRID_1 + id + JSON_READER_SUB_GRID_2 + id + JSON_READER_SUB_GRID_3 + id
						+ JSON_READER_SUB_GRID_4 + "rowNum:" + rowsPerPage;
				StringBuffer subGridColModel = loColModel;
				// Start: Updated in Release 6
				if (StringUtils.isNotBlank(subGridRowNumbers))
				{
					lsSubGridFnData = lsSubGridFnData + ",rownumbers :true,rownumWidth: "
							+ (Integer.parseInt(subGridRowNumbers) - 1);
					subGridColModel.replace(3, 30, "");
				}
				lsSubGridFnData = lsSubGridFnData + ",colModel: [" + subGridColModel.toString().replace("{},", "")
						+ "}]," + STR_JS_10 + autoWidth + STR_JS_11 + cellUrl + "',editurl:'" + editUrl + "',"
						+ "gridview: true," + LOAD_ERROR_1 + id + LOAD_ERROR_2 + LOAD_COMPLETE_SUB_GRID_1 + id
						+ LOAD_COMPLETE_SUB_GRID_2 + id + LOAD_COMPLETE_SUB_GRID_3 + id + LOAD_COMPLETE_SUB_GRID_4
						+ loCurrencyInfo + LOAD_COMPLETE_SUB_GRID_5 + id + LOAD_COMPLETE_SUB_GRID_6
						+ lsCallBackFnAfteLoadGrid + "removePageGreyOut();}," + ON_DBL_CLICK_ROW
						+ ON_SELECT_ROW_SUB_GRID + STR_JS_12 + lsPagination;
				lsPagination = "";
				// End: Updated in Release 6
			}
			/* javascript Start */
			// Start: Updated in Release 6
			String lsStrJQuery = STR_JS_1 + "var totalLastCol = \"" + columnTotalName + "\";var notAllowDuplicate = \""
					+ notAllowDuplicateColumn 
					+ "\"; var subGridRowNumber=" + subGridRowNumbers
					+ ";"
					//Start: Added in R7 
					+ " var modFlag = [];"
					//End: Added in R7 
					+ "var exportFileName = '" + exportFileName + "';var notEditableForAddRow = \""
					+ nonEditColumnName + "\";" + "var lastRowEdit=\"" + lastRowEdit + "\";"
					+ "var checkForTotalValue = \"" + checkForTotalValue
					+ "\";checkForTotalValue = checkForTotalValue.split(',');var isGridReadOnly='" + isReadOnly
					+ "';var isNewRecordDelete = \"" + isNewRecordDelete + "\"; var negativeCurrency= \""
					+ negativeCurrency + "\";var positiveCurrency=\"" + positiveCurrency + "\";" + STR_JS_2
					+ lsTableName + STR_JS_3 + STR_JS_4 + id + RELOAD + lsCallBackFn + STR_JS_5 + checkForZeroAndDelete
					+ "\";" + STR_JS_6 + lsMsgKey + "';" + STR_JS_6_1 + dropDownData + "\";" + ADD_ROW_DATA
					+ EXTEND_ADD_ROW_DATA 
					//Start: Added in R7
					+ FUNCTION_1.replace("mod_table_id", id) + id 
					//End:Added in R7
					+ FUNCTION_2 + id + FUNCTION_3  + id + FUNCTION_4 + id + FUNCTION_X + id
					+ FUNCTION_5 + id + FUNCTION_6 +  id + FUNCTION_7 + id + FUNCTION_8
					+ id
					+ FUNCTION_9
					+ id
					+ FUNCTION_10  
					+ id    
					+ FUNCTION_11
					+ id
					+ FUNCTION_12
					+ id
					+ FUNCTION_13
					+ id
					+ FUNCTION_14
					+ id
					// Updated in R6
					+ FUNCTION_15 + FUNCTION_15_1 + id + FUNCTION_15_2 + id + FUNCTION_15_3 + id + FUNCTION_15_4 + id
					+ FUNCTION_14 + id + FUNCTION_15 + Function_15_7 + lsNavgrid + FUNCTION_16 + lsCallBackFn
					+ FUNCTION_17_1 + FUNCTION_17_1_4 + operations + FUNCTION_17 + showExportButton(exportFileName)
					+ FUNCTION_17_5 + "$('#table_" + id + "').jqGrid({url: '" + gridUrl + "'" + ",datatype: '"
					+ dataType + "',mtype: '" + methodType + "',cellurl :'" + cellUrl + "',editurl :'" + editUrl
					+ "',colNames:[" + gridColNames + "],ajaxSelectOptions: { cache: false }," + loGridFnData
					+ STR_JS_7 + JSON_READER_GRID_1 + id + JSON_READER_GRID_2 + id + JSON_READER_GRID_3 + id
					+ JSON_READER_GRID_4 + STR_JS_7_1 + isSubGrid + lsPager + ",height: '100%'" + ", autowidth:"
					+ autoWidth + lsSubGridFnData + "});" + "if(!" + isSubGrid + "){getPagerDetails('table_" + id
					+ "','pager_" + id + "');}" + lsPagination + "});</script>" + "<body><div id='serverError" + id
					+ "' style='color: red; font-weight: bold;'></div><div id='loadError" + id
					+ "' style='color: red; font-weight: bold;'></div><table id='table_" + id
					+ "' class='scroll' cellpadding='0' cellspacing='0'></table>" + "<div id='pager_" + id
					+ "' class='scroll' style='text-align: center;'></div></body>" + getExportForm(id, exportFileName);
			/* javascript End */

/*			LOG_OBJECT.Info( "\n########Start-JQgridTag   " +
					lsStrJQuery  + "\n########End-JQgridTag  ");*/

			// End: Updated in Release 6
			loWriterObj
					.write(appendIdToStr(lsStrJQuery
							.replaceAll(
									"custom:true,custom_func:allowOnlyPositiveValue,required:true,number:true,custom:true,custom_func:allowOnlyPositiveValue",
									"required:true,number:true,custom:true,custom_func:allowOnlyPositiveValue")
							.replaceAll(
									"custom:true,custom_func:allowOnlyNegativeValue,required:true,number:true,custom:true,custom_func:allowOnlyPositiveValue",
									"required:true,number:true,custom:true,custom_func:allowOnlyNegativeValue")
							.replaceAll(
									"custom:true,custom_func:allowOnlyPercentValue,required:true,number:true,custom:true,custom_func:allowOnlyPercentValue",
									"custom:true,custom_func:allowOnlyPercentValue,required:true,number:true")));
		}
		catch (IOException loExp)
		{
			LOG_OBJECT.Error("Error occured while JQGRID processing : ", loExp);
		}
		return super.doStartTag();
	}

	/**
	 * This method will return csv download file for Export functionality on PS
	 * Enhancement. Added in Release 6
	 */
	private String getExportForm(String aoTableId, String aoExportFileName)
	{
		StringBuffer loExportForm = new StringBuffer();
		if (StringUtils.isNotBlank(aoExportFileName))
		{
			loExportForm
					.append("<form action='/HHSPortal/GetContent.jsp?alertAction=downloadCsv' method='post' id='exportFileForm'>");
			loExportForm.append("<input type='hidden' id='csvFileName' name='csvFileName'>");
			loExportForm.append("<input type='hidden' id='csvRowData' name='csvRowData'>");
			loExportForm.append("</form>");
		}
		return loExportForm.toString();
	}

	/**
	 * This method will return for Export functionality for PS Enhancement.
	 * Added in Release 6
	 */
	private String showExportButton(String aoExportFileName)
	{
		StringBuffer loExport = new StringBuffer();
		if (StringUtils.isNotBlank(aoExportFileName))
		{
			loExport.append(FUNCTION_17_1_1 + aoExportFileName + FUNCTION_17_1_2 + id + FUNCTION_17_1_3);
		}
		return loExport.toString();
	}

	/**
	 * This method will be jqgrid's operation on the basis of tld's attribute
	 */
	private void getOperation()
	{
		if (null != isReadOnly && isReadOnly.equalsIgnoreCase(HHSConstants.TRUE))
		{
			lsTempColModel = loColModel.toString().replace("editable:true", "editable:false");
			loColModel = new StringBuffer();
			loColModel.append(lsTempColModel);
			operations = operations.replace(HHSConstants.TRUE, HHSConstants.FALSE);
		}
		else
		{
			LOG_OBJECT.Info("Grid is not Read Only");
		}
		if (operations.contains("del:true"))
		{
			lsNavgrid = "del:true,";
			operations.replace("del:true,", " ");
		}
		else
		{
			lsNavgrid = "del:false,";
			operations.replace("del:false,", " ");
		}
		if (isPagination.equals(HHSConstants.FALSE))
		{
			if (isSubGrid.equals(HHSConstants.TRUE))
			{
				lsPagination = "$('#p_'+subgrid_table_id+'_center').hide();}";
			}
			else
			{
				lsPagination = "$('#pager_" + id + "_center').hide();";
			}
		}
		else
		{
			if (isSubGrid.equals(HHSConstants.TRUE))
			{
				lsPagination = "}";
			}
			else
			{
				lsPagination = "";
			}

		}
	}

	/**
	 * This method will be jqgrid's colmodel on the basis of tld's attribute
	 */
	private void getColModel()
	{
		lsTemp1 = gridColProp;
		lsTemp1 = lsTemp1.replaceAll("\\s", "");
		lsTemp1 = lsTemp1.replace("},{", "||");
		lsTemp1 = lsTemp1.substring(1, lsTemp1.length() - 1);
		lsTemp1 = lsTemp1.replace("currencyTemplate", "currencyTemplate");
		String lsTemp2 = subGridColProp;
		lsTemp2 = lsTemp2.replaceAll("\\s", "");
		lsTemp2 = lsTemp2.replace("},{", "||");
		lsTemp2 = lsTemp2.replace("allowOnlyPositiveValue", "custom:true,custom_func:allowOnlyPositiveValue");
		lsTemp2 = lsTemp2.replace("allowOnlyNegativeValue", "custom:true,custom_func:allowOnlyNegativeValue");
		lsTemp2 = lsTemp2.replace("allowOnlyPercentValue", "custom:true,custom_func:allowOnlyPercentValue");
		lsTemp2 = lsTemp2.replace("notAllowDuplicateValue", "custom:true,custom_func:isCOFDuplicate");
		lsTemp2 = lsTemp2.replace("notAllowDuplicateforOther", "custom:true,custom_func:notAllowDuplicateforOther");
		lsTemp2 = lsTemp2.replace("allowBothSignCurrencyValue", "custom:true,custom_func:allowBothSignCurrencyValue");
		lsTemp2 = lsTemp2.replace("isMandatoryField", "custom:true,custom_func:isMandatoryField");
		lsTemp2 = lsTemp2.substring(1, lsTemp2.length() - 1);
		StringTokenizer loToken1 = new StringTokenizer(lsTemp1, "||");
		StringTokenizer loToken2 = new StringTokenizer(lsTemp2, "||");
		int liCount = 0;
		while (loToken1.hasMoreTokens())
		{
			StringBuffer loTmp = new StringBuffer(loToken1.nextToken());
			updateFormatType(loTmp);
			if (null != isCOAScreen && isCOAScreen.equalsIgnoreCase(HHSConstants.TRUE))
			{
				if (loTmp.toString().contains("uobc"))
				{
					loTmp.append(",width:120");
				}
				else if (loTmp.toString().contains("subOC") || loTmp.toString().contains("rc"))
				{
					loTmp.append(",width:70");
				}

				else if (loTmp.toString().contains("fy"))
				{
					loTmp.append(",width:110");
				}
				else if (loTmp.toString().contains("fundingType"))
				{
					loTmp.append(",width:270");
				}
				else
				{
					loTmp.append(",width:250");
				}
				liCount++;
			}
			if (loColModel != null)
			{
				if (!modificationType.isEmpty())
				{
					loColModel = updateColModel(loColModel, loTmp, lsEditRule, loToken2.nextToken());
				}
				else
				{
					loColModel.append("},{");
					loColModel.append(loTmp);
					// Start: Updated in Release 6, to skip rownum column
					if (!loTmp.toString().contains("rownum"))
					{
						loColModel.append(",");
						loColModel.append(loToken2.nextToken());
					}
					// End: Updated in Release 6
				}
			}
		}
		// Logic for dynamic autowidth in case of COA/COF
		dynamicWidth(liCount);
	}

	/**
	 * This method will be update autowidth for COA/COF
	 */
	private void dynamicWidth(int locount)
	{
		if (null != isCOAScreen && isCOAScreen.equalsIgnoreCase(HHSConstants.TRUE))
		{
			if (locount <= 6)
			{
				autoWidth = HHSConstants.TRUE;
			}
			else
			{
				autoWidth = HHSConstants.FALSE;
			}
		}
	}

	/**
	 * This method will be update the loCurrencyInfo variable
	 */
	private void updateFormatType(StringBuffer loTmp)
	{
		if (loTmp.toString().contains("numberTemplate"))
		{
			loCurrencyInfo.append(",");
			loCurrencyInfo.append("numberTemplate");
		}
		else if (loTmp.toString().contains("percentageTemplate"))
		{
			loCurrencyInfo.append(",");
			loCurrencyInfo.append("percentageTemplate");
		}
		else if (loTmp.toString().contains("integerTemplate"))
		{
			loCurrencyInfo.append(",");
			loCurrencyInfo.append("integerTemplate");
		}
		else if (loTmp.toString().contains("currencyTemplate"))
		{
			loCurrencyInfo.append(",");
			loCurrencyInfo.append("currencyTemplate");
		}
		//Added in R7 for program income grid
		else if (loTmp.toString().contains("negativeModificationTemplate"))
		{
			loCurrencyInfo.append(",");
			loCurrencyInfo.append("negativeModificationTemplate");
		}
		else
		{
			loCurrencyInfo.append(",");
			loCurrencyInfo.append("noTemplate");
		}
	}

	/**
	 * This method will be give jqgrid's initiale variable Updated in Release 6
	 */
	private void checkInitialVariable()
	{
		// Start: Added in Release 6
		if (null == callbackFnAfterLoadGrid)
		{
			lsCallBackFnAfteLoadGrid = "";
		}
		else
		{
			lsCallBackFnAfteLoadGrid = callbackFnAfterLoadGrid;
		}
		// End: Added in Release 6
		if (null == callbackFunction)
		{
			lsCallBackFn = "";
		}
		else
		{
			lsCallBackFn = callbackFunction;
		}
		if (null == autoWidth)
		{
			autoWidth = HHSConstants.TRUE;
		}
		if (autoWidth.equalsIgnoreCase(HHSConstants.FALSE))
		{
			autoWidth = HHSConstants.FALSE;
		}
		if (null == isCOAScreen || isCOAScreen.equalsIgnoreCase(HHSConstants.FALSE))
		{
			isCOAScreen = HHSConstants.FALSE;
		}
		if (null != modificationType && !modificationType.isEmpty())
		{
			lsMsgKey = modificationType.substring(9);
			if (modificationType.contains("positive"))
			{
				lsEditRule = "editrules:{custom:true,custom_func:allowOnlyPositiveValue,";
			}
			else if (modificationType.contains("negative"))
			{
				lsEditRule = "editrules:{custom:true,custom_func:allowOnlyNegativeValue,";
			}
			else
			{
				LOG_OBJECT.Info("JQGrid Modification Type : '" + modificationType + "' is Invalid");
				modificationType = "";
			}
		}
		else
		{
			LOG_OBJECT.Info("JQGrid Attibute for Modification Type is optional");
			modificationType = "";
		}
	}

	/**
	 * This method will be append the global variable with tld's ID attribute
	 * <b>Updated in Release 6</b>
	 * @param strJQuery jquery's String
	 * @return lsNewJqueryStr appended jquery's String
	 */
	private String appendIdToStr(String strJQuery)
	{
		String lsId = id.replace("-", "ID");
		String lsHeaderData = "headerData" + lsId;
		String lsDuplicateValue = "DV" + lsId;
		String lsCurrentRowInfo = "currentRowInfo" + lsId;
		String lsParentTotal = "parentTotal" + lsId;
		String lsCheckForTotalValue = "checkForTotalValue" + lsId;
		String lsCheckForTotalValueData = "checkForTotalValueData" + lsId;
		String lsCheckForZeroAndDelete = "checkForZeroAndDelete" + lsId;
		String lsTotalSign = "totalSign" + lsId;
		String lsTotalLastCol = "totalLastCol" + lsId;
		// Start: Added in Release 6
		String lsSubGridRowNumber = "subGridRowNumber" + lsId;
		String lsExportFileName = "exportFileName" + lsId;
		// End: Added in Release 6
		String lsTotalColumn = "totalColumn" + lsId;
		String lsGetEditableFields = "getEditableFields" + lsId;
		String lsEdtableRows = "edtableRows" + lsId;
		String lsColLen = "colLen" + lsId;
		String lsLastsel = "lastsel" + lsId;
		String lsPagerId = "pager_id" + lsId;
		String lsClickOnce = "clickOnce" + lsId;
		String lsSubGridTableId = "subgrid_table_id" + lsId;
		String lsCurrentId = "currentId" + lsId;
		String lsApplyStyle = "applyStyle" + lsId;
		String lsEditButton = "editButton" + lsId;
		String lsEditButtonData = "editButtonData" + lsId;
		String lsMyEditOptions = "myEditOptions" + lsId;
		String lsNotAllowDuplicate = "notAllowDuplicate" + lsId;
		String lsDduplicateValue = "duplicateValue" + lsId;
		String lsNotEditableForAddRow = "notEditableForAddRow" + lsId;
		String lsLastRowEdit = "lastRowEdit" + lsId;
		String lsIsNewRecordDelete = "isNewRecordDelete" + lsId;
		String lsGridPage = "gridPage" + lsId;
		String lsGlobalUrl = "globalUrl" + lsId;
		String lsEditPage = "editPage" + lsId;
		String lsCustomOnlyPositiveValue = ":allowOnlyPositiveValue" + lsId;
		String lsCustomOnlyNegativeValue = ":allowOnlyNegativeValue" + lsId;
		String lsCustomOnlyPercentValue = ":allowOnlyPercentValue" + lsId;
		String lsMandatoryField = ":isMandatoryField" + lsId;
		String lsFuncGetColName = "getColName" + lsId;
		String lsFuncGetCurrEditRowVal = "getCurrEditRowVal" + lsId;
		String lsFuncGetSubGridIds = "getSubGridIds" + lsId;
		String lsFuncNotAllowDuplicateforOther = "notAllowDuplicateforOther" + lsId;
		String lsIsCOFDuplicate = "isCOFDuplicate" + lsId;
		String lsFuncAllowOnlyPositiveValue = "n allowOnlyPositiveValue" + lsId;
		String lsFuncAllowOnlyNegativeValue = "n allowOnlyNegativeValue" + lsId;
		String lsFuncAllowOnlyPercentValue = "n allowOnlyPercentValue" + lsId;
		String lsFuncIsMandatoryField = "n isMandatoryField" + lsId;
		String lsFuncHeaderNameFromColname = "getHeaderNameFromColname" + lsId;
		String lsFuncUpdateParentGrid = "updateParentGrid" + lsId;
		String lsFuncTotalLastColumn = "totalLastColumn" + lsId;
		String lsFuncGetPagerDetails = "getPagerDetails" + lsId;
		String lsFuncGetEditableFieldsFn = "getEditableFieldsFn" + lsId;
		String lsJQGridFuncOldAddRowData = "oldAddRowData" + lsId;
		String lsJQGridFuncOldInfoDialog = "oldInfoDialog" + lsId;
		String lsJQGridFuncOldEditRow = "oldEditRow" + lsId;
		String lsIsArrayContainsDuplicateValue = "isArrayContainsDuplicateValue" + lsId;
		String lsPositiveCurrency = "positiveCurrency" + lsId;
		String lsNegativeCurrency = "negativeCurrency" + lsId;
		String lsNewJqueryStr = strJQuery
				.replaceAll("duplicateValue", lsDuplicateValue)
				.replaceAll("headerData", lsHeaderData)
				.replaceAll(":isMandatoryField", lsMandatoryField)
				.replaceAll("n isMandatoryField", lsFuncIsMandatoryField)
				.replaceAll("getHeaderNameFromColname", lsFuncHeaderNameFromColname)
				.replaceAll("currentRowInfo", lsCurrentRowInfo)
				.replaceAll("parentTotal", lsParentTotal)
				.replaceAll("checkForTotalValue", lsCheckForTotalValue)
				.replaceAll("checkForTotalValueData", lsCheckForTotalValueData)
				.replaceAll("checkForZeroAndDelete", lsCheckForZeroAndDelete)
				.replaceAll("totalSign", lsTotalSign)
				// Start: Updated in Release 6
				.replaceAll("totalLastCol", lsTotalLastCol)
				.replaceAll("subGridRowNumber", lsSubGridRowNumber)
				.replaceAll("exportFileName", lsExportFileName)
				.replaceAll("totalColumn", lsTotalColumn)
				// End: Updated in Release 6
				.replaceAll("getEditableFields", lsGetEditableFields).replaceAll("edtableRows", lsEdtableRows)
				.replaceAll("colLen", lsColLen).replaceAll("lastsel", lsLastsel).replaceAll("pager_id", lsPagerId)
				.replaceAll("clickOnce", lsClickOnce).replaceAll("subgrid_table_id", lsSubGridTableId)
				.replaceAll("currentId", lsCurrentId).replaceAll("applyStyle", lsApplyStyle)
				.replaceAll("editButton", lsEditButton).replaceAll("editButtonData", lsEditButtonData)
				.replaceAll("myEditOptions", lsMyEditOptions).replaceAll("notAllowDuplicate", lsNotAllowDuplicate)
				.replaceAll("duplicateValue", lsDduplicateValue)
				.replaceAll("notEditableForAddRow", lsNotEditableForAddRow).replaceAll("lastRowEdit", lsLastRowEdit)
				.replaceAll("isNewRecordDelete", lsIsNewRecordDelete)
				.replaceAll(":allowOnlyPositiveValue", lsCustomOnlyPositiveValue)
				.replaceAll(":allowOnlyNegativeValue", lsCustomOnlyNegativeValue)
				.replaceAll(":allowOnlyPercentValue", lsCustomOnlyPercentValue)
				.replaceAll("getColName", lsFuncGetColName).replaceAll("getCurrEditRowVal", lsFuncGetCurrEditRowVal)
				.replaceAll("getSubGridIds", lsFuncGetSubGridIds)
				.replaceAll("notAllowDuplicateforOther", lsFuncNotAllowDuplicateforOther)
				.replaceAll("isCOFDuplicate", lsIsCOFDuplicate)
				.replaceAll("n allowOnlyPositiveValue", lsFuncAllowOnlyPositiveValue)
				.replaceAll("n allowOnlyNegativeValue", lsFuncAllowOnlyNegativeValue)
				.replaceAll("n allowOnlyPercentValue", lsFuncAllowOnlyPercentValue)
				.replaceAll("updateParentGrid", lsFuncUpdateParentGrid)
				.replaceAll("totalLastColumn", lsFuncTotalLastColumn)
				.replaceAll("getPagerDetails", lsFuncGetPagerDetails)
				.replaceAll("getEditableFieldsFn", lsFuncGetEditableFieldsFn)
				.replaceAll("oldAddRowData", lsJQGridFuncOldAddRowData)
				.replaceAll("oldInfoDialog", lsJQGridFuncOldInfoDialog)
				.replaceAll("oldEditRow", lsJQGridFuncOldEditRow).replaceAll("gridPage", lsGridPage)
				.replaceAll("globalUrl", lsGlobalUrl).replaceAll("editPage", lsEditPage)
				.replaceAll("isArrayContainsDuplicateValue", lsIsArrayContainsDuplicateValue)
				.replaceAll("positiveCurrency", lsPositiveCurrency).replaceAll("negativeCurrency", lsNegativeCurrency);
		return lsNewJqueryStr;
	}

	/**
	 * This method will be jqgrid's operation on the basis of tld's attribute
	 * @param _colModel colmodel stringbuffer
	 * @param tmp temporary stringbuffer
	 * @param editRule editrule param
	 * @param token2 editrule param
	 * @return _colModel stringBuffer
	 */
	private StringBuffer updateColModel(StringBuffer locolModel, StringBuffer tmp, String editRule, String token2)
	{
		if (locolModel != null)
		{
			if (tmp.toString().contains("template") && !tmp.toString().contains("percentageTemplate"))
			{
				locolModel.append("},{");
				locolModel.append(tmp);
				locolModel.append(",");
				locolModel.append(token2.replace("editrules:{", editRule));
			}
			else if (tmp.toString().contains("percentageTemplate"))
			{
				locolModel.append("},{");
				locolModel.append(tmp);
				locolModel.append(",");
				locolModel.append(token2.replace("editrules:{",
						"editrules:{custom:true,custom_func:allowOnlyPercentValue,"));
			}
			else
			{
				locolModel.append("},{");
				locolModel.append(tmp);
				locolModel.append(",");
				locolModel.append(token2);
			}
		}
		else
		{
			if (tmp.toString().contains("template") && !tmp.toString().contains("percentageTemplate"))
			{
				locolModel = new StringBuffer();
				locolModel.append(tmp);
				locolModel.append(",");
				locolModel.append(token2.replace("editrules:{", editRule));
			}
			else if (tmp.toString().contains("percentageTemplate"))
			{
				locolModel = new StringBuffer();
				locolModel.append(tmp);
				locolModel.append(",");
				locolModel.append(token2.replace("editrules:{",
						"editrules:{custom:true,custom_func:allowOnlyPercentValue,"));
			}
			else
			{
				locolModel = new StringBuffer();
				locolModel.append(tmp);
				locolModel.append(",");
				locolModel.append(token2);
			}
		}
		return locolModel;
	}

	/****************************
	 * Generate Getter & Setter *
	 ****************************/

	/**
	 * @return the nonEditColumnName
	 */
	public String getNonEditColumnName()
	{
		return nonEditColumnName;
	}

	/**
	 * @param nonEditColumnName the nonEditColumnName to set
	 */
	public void setNonEditColumnName(String nonEditColumnName)
	{
		this.nonEditColumnName = nonEditColumnName;
	}

	/**
	 * @return the gridUrl
	 */
	public String getGridUrl()
	{
		return gridUrl;
	}

	/**
	 * @param gridUrl the gridUrl to set
	 */
	public void setGridUrl(String gridUrl)
	{
		this.gridUrl = gridUrl;
	}

	/**
	 * @return the editUrl
	 */
	public String getEditUrl()
	{
		return editUrl;
	}

	/**
	 * @param editUrl the editUrl to set
	 */
	public void setEditUrl(String editUrl)
	{
		this.editUrl = editUrl;
	}

	/**
	 * @return the subGridUrl
	 */
	public String getSubGridUrl()
	{
		return subGridUrl;
	}

	/**
	 * @param subGridUrl the subGridUrl to set
	 */
	public void setSubGridUrl(String subGridUrl)
	{
		this.subGridUrl = subGridUrl;
	}

	/**
	 * @return the cellUrl
	 */
	public String getCellUrl()
	{
		return cellUrl;
	}

	/**
	 * @param cellUrl the cellUrl to set
	 */
	public void setCellUrl(String cellUrl)
	{
		this.cellUrl = cellUrl;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType()
	{
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}

	/**
	 * @return the methodType
	 */
	public String getMethodType()
	{
		return methodType;
	}

	/**
	 * @param methodType the methodType to set
	 */
	public void setMethodType(String methodType)
	{
		this.methodType = methodType;
	}

	/**
	 * @return the gridColNames
	 */
	public String getGridColNames()
	{
		return gridColNames;
	}

	/**
	 * @param gridColNames the gridColNames to set Updated in Release 6
	 */
	public void setGridColNames(String gridColNames)
	{
		// Start: Added in Release 6
		if (StringUtils.isNotBlank(subGridRowNumbers))
		{
			String blankColName = "' ',";
			this.gridColNames = blankColName + gridColNames;
		}
		else
		{
			this.gridColNames = gridColNames;
		}
		// End: Added in Release 6
	}

	/**
	 * @return the gridColProp
	 */
	public String getGridColProp()
	{
		return gridColProp;
	}

	/**
	 * @param gridColProp the gridColProp to set Updated in Release 6
	 */
	public void setGridColProp(String gridColProp)
	{
		// Start: Added in Release 6
		if (StringUtils.isNotBlank(subGridRowNumbers))
		{
			String blankColProp = "{name:'rownum',width:'" + subGridRowNumbers + "'},";
			this.gridColProp = blankColProp + gridColProp;
		}
		else
		{
			this.gridColProp = gridColProp;
		}
		// End: Added in Release 6
	}

	/**
	 * @return the subGridColProp
	 */
	public String getSubGridColProp()
	{
		return subGridColProp;
	}

	/**
	 * @param subGridColProp the subGridColProp to set
	 */
	public void setSubGridColProp(String subGridColProp)
	{
		this.subGridColProp = subGridColProp;
	}

	/**
	 * @return the columnTotalName
	 */
	public String getColumnTotalName()
	{
		return columnTotalName;
	}

	/**
	 * @param columnTotalName the columnTotalName to set
	 */
	public void setColumnTotalName(String columnTotalName)
	{
		this.columnTotalName = columnTotalName;
	}

	/**
	 * @return the isSubGrid
	 */
	public String getIsSubGrid()
	{
		return isSubGrid;
	}

	/**
	 * @param isSubGrid the isSubGrid to set
	 */
	public void setIsSubGrid(String isSubGrid)
	{
		this.isSubGrid = isSubGrid;
	}

	/**
	 * @return the operations
	 */
	public String getOperations()
	{
		return operations;
	}

	/**
	 * @param operations the operations to set
	 */
	public void setOperations(String operations)
	{
		this.operations = operations;
	}

	/**
	 * @return the rowsPerPage
	 */
	public String getRowsPerPage()
	{
		return rowsPerPage;
	}

	/**
	 * @param rowsPerPage the rowsPerPage to set
	 */
	public void setRowsPerPage(String rowsPerPage)
	{
		this.rowsPerPage = rowsPerPage;
	}

	/**
	 * @return the isPagination
	 */
	public String getIsPagination()
	{
		return isPagination;
	}

	/**
	 * @param isPagination the isPagination to set
	 */
	public void setIsPagination(String isPagination)
	{
		this.isPagination = isPagination;
	}

	/**
	 * @return the isReadOnly
	 */
	public String getIsReadOnly()
	{
		return isReadOnly;
	}

	/**
	 * @param isReadOnly the isReadOnly to set
	 */
	public void setIsReadOnly(String isReadOnly)
	{
		this.isReadOnly = isReadOnly;
	}

	/**
	 * @return the modificationType
	 */
	public String getModificationType()
	{
		return modificationType;
	}

	/**
	 * @param modificationType the modificationType to set
	 */
	public void setModificationType(String modificationType)
	{
		if (null != modificationType && !HHSConstants.EMPTY_STRING.equalsIgnoreCase(modificationType))
		{
			String[] lsArray = modificationType.split(HHSConstants.COMMA);
			String lsMsg = HHSConstants.EMPTY_STRING;
			try
			{
				lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, lsArray[1]);
			}
			catch (ApplicationException loExp)
			{
				LOG_OBJECT.Error("Error occured on setModificationType attribute : ", loExp);
			}
			modificationType = lsArray[0] + HHSConstants.COMMA + lsMsg;
		}
		this.modificationType = modificationType;
	}

	/**
	 * @return the lastRowEdit
	 */
	public String getLastRowEdit()
	{
		return lastRowEdit;
	}

	/**
	 * @param lastRowEdit the lastRowEdit to set
	 */
	public void setLastRowEdit(String lastRowEdit)
	{
		this.lastRowEdit = lastRowEdit;
	}

	/**
	 * @return the checkForTotalValue
	 */
	public String getCheckForTotalValue()
	{
		return checkForTotalValue;
	}

	/**
	 * @param checkForTotalValue the checkForTotalValue to set
	 */
	public void setCheckForTotalValue(String checkForTotalValue)
	{
		if (null != checkForTotalValue && !HHSConstants.EMPTY_STRING.equalsIgnoreCase(checkForTotalValue))
		{
			String[] lsArray = checkForTotalValue.split(HHSConstants.COMMA);
			String lsMsg = HHSConstants.EMPTY_STRING;
			try
			{
				lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, lsArray[2]);
			}
			catch (ApplicationException loExp)
			{
				LOG_OBJECT.Error("Error occured on setCheckForTotalValue attribute : ", loExp);
			}
			checkForTotalValue = lsArray[0] + HHSConstants.COMMA + lsArray[1] + HHSConstants.COMMA + lsMsg;
		}
		this.checkForTotalValue = checkForTotalValue;
	}

	/**
	 * @return the nonEditColumnName
	 */
	public String getCheckForZeroAndDelete()
	{
		return checkForZeroAndDelete;
	}

	/**
	 * @param checkForZeroAndDelete the notAllowDuplicateColumn to set
	 */
	public void setCheckForZeroAndDelete(String checkForZeroAndDelete)
	{
		if (null != checkForZeroAndDelete && !HHSConstants.EMPTY_STRING.equalsIgnoreCase(checkForZeroAndDelete))
		{
			String[] lsArray = checkForZeroAndDelete.split(HHSConstants.COMMA);
			String lsMsg = HHSConstants.EMPTY_STRING;
			try
			{
				lsMsg = PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, lsArray[1]);
			}
			catch (ApplicationException loExp)
			{
				LOG_OBJECT.Error("Error occured on setCheckForZeroAndDelete attribute : ", loExp);
			}
			checkForZeroAndDelete = lsArray[0] + HHSConstants.COMMA + lsMsg;
		}
		this.checkForZeroAndDelete = checkForZeroAndDelete;
	}

	/**
	 * @return the notAllowDuplicateColumn
	 */
	public String getNotAllowDuplicateColumn()
	{
		return notAllowDuplicateColumn;
	}

	/**
	 * @param notAllowDuplicateColumn the notAllowDuplicateColumn to set
	 */
	public void setNotAllowDuplicateColumn(String notAllowDuplicateColumn)
	{
		this.notAllowDuplicateColumn = notAllowDuplicateColumn;
	}

	/**
	 * @return the autoWidth
	 */
	public String getAutoWidth()
	{
		return autoWidth;
	}

	/**
	 * @param autoWidth the autoWidth to set
	 */
	public void setAutoWidth(String autoWidth)
	{
		this.autoWidth = autoWidth;
	}

	/**
	 * @return the isCOAScreen
	 */
	public String getIsCOAScreen()
	{
		return isCOAScreen;
	}

	/**
	 * @param isCOAScreen the isCOAScreen to set
	 */
	public void setIsCOAScreen(String isCOAScreen)
	{
		this.isCOAScreen = isCOAScreen;
	}

	/**
	 * @return the isNewRecordDelete
	 */
	public String getIsNewRecordDelete()
	{
		return isNewRecordDelete;
	}

	/**
	 * @param isNewRecordDelete the isNewRecordDelete to set
	 */
	public void setIsNewRecordDelete(String isNewRecordDelete)
	{
		this.isNewRecordDelete = isNewRecordDelete;
	}

	/**
	 * @return the callbackFunction
	 */
	public String getCallbackFunction()
	{
		return callbackFunction;
	}

	/**
	 * @param callbackFunction the callbackFunction to set
	 */
	public void setCallbackFunction(String callbackFunction)
	{
		this.callbackFunction = callbackFunction;
	}

	/**
	 * @return the negativeCurrency
	 */
	public String getNegativeCurrency()
	{
		return negativeCurrency;
	}

	/**
	 * @param negativeCurrency the negativeCurrency to set
	 */
	public void setNegativeCurrency(String negativeCurrency)
	{
		this.negativeCurrency = negativeCurrency;
	}

	/**
	 * @return the positiveCurrency
	 */
	public String getPositiveCurrency()
	{
		return positiveCurrency;
	}

	/**
	 * @param positiveCurrency the positiveCurrency to set
	 */
	public void setPositiveCurrency(String positiveCurrency)
	{
		this.positiveCurrency = positiveCurrency;
	}

	/**
	 * @return the dropDownData
	 */
	public String getDropDownData()
	{
		return dropDownData;
	}

	/**
	 * @param dropDownData the dropDownData to set
	 */
	public void setDropDownData(String dropDownData)
	{
		this.dropDownData = dropDownData;
	}

	/**
	 * @return the isExpandOnLoad
	 */
	public String getIsExpandOnLoad()
	{
		if (null != isExpandOnLoad && isExpandOnLoad.equals("true"))
		{
			return isExpandOnLoad;
		}
		return "false";
	}

	/**
	 * @param isExpandOnLoad the isExpandOnLoad to set
	 */
	public void setIsExpandOnLoad(String isExpandOnLoad)
	{
		this.isExpandOnLoad = isExpandOnLoad;
	}
}
