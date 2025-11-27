 //This method is used to hide unhide tabs.
	function showme(id, linkid) {
	    var divid = document.getElementById(id);
	    var toggleLink = document.getElementById(linkid);
	    if (divid.style.display == '') {
	        toggleLink.innerHTML = '+';
	       divid.style.display = 'none';   
		}
	    else {
	        toggleLink.innerHTML = '-';
	        divid.style.display = '';
	    }
	}
	//This method is used for currency check on 
	//invoice summary line items properties
	$(document).ready(function() {
		$(".invoiceCurrencyCheck").each(function(e) {
			$(this).validateCurrencyOnLoad();
		});
	});