/**
* Gets the fy budget planned mount for current fiscal year
* This Overide the JQGrid's Edit Row
*/
var oldEditRow = $.fn.jqGrid.editRow;
$.jgrid.extend({
	editRow: function (iRow,iCol, ed){
		if(iRow == 'new_row'){
			$(this).jqGrid('setColProp','total',{editable : false});
		}
		else if(iRow.indexOf('_newrecord') !== -1){
			var totalColumn = getSubGridIds(iRow);
            var tempStringlen = totalColumn.split(',');
            $(this).jqGrid('setColProp',tempStringlen[0], {editable : true});
		}
		else if(iRow.indexOf('-Federal') !== -1 || iRow.indexOf('-State') !== -1 || iRow.indexOf('-City') !== -1 || iRow.indexOf('-Other') !== -1){
			// This is required to make optional grid's Fiscal year editable
		}
		else{
			var totalColumn = getSubGridIds(iRow);
            var tempStringlen = totalColumn.split(',');
            for(var i=0;i<3;i++)
            	$(this).jqGrid('setColProp',tempStringlen[i], {editable : false});
			$(this).jqGrid('setColProp','subbudgetName', {editable : false});
		}
       return oldEditRow.call (this, iRow, iCol, ed); 
   }
});

/**
 *  This function is to get total grid'd Id
*/
function getSubGridIds(rowid){ 
	var tempString='';
	$('#'+rowid+'>td').each(function(i){
		var finalId = getColName(this);
		if(tempString != ''){
			tempString = tempString + ','+ finalId;
		}else{
			tempString = finalId;
		}
	});
return tempString;
}

/**
 * function to expand and collapse headers
 * */
function showme(id, linkid) {
	var divid = document.getElementById(id);
	var toggleLink = document.getElementById(linkid);
	if (divid.style.display == '') {
		toggleLink.innerHTML = '+';
		divid.style.display = 'none';
	} else {
		toggleLink.innerHTML = '-';
		divid.style.display = '';
	}
}

/**
 * method to view printer friendly version
 * */
function View() {
	var a_href = $("#printerViewCB").attr('href') + "&removeMenu=";
	window.open(a_href);
}



	
