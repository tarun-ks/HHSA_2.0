<%@ page errorPage="/error/errorpage.jsp" %>
<script type="text/javascript">

	$(document).ready(function() {   	
		$("a.exit-panel").click(function(){					
			$(".alert-box").hide();
			$(".overlay").hide();
			});				   
	});
</script>
<!-- Overlay Popup Starts -->

<div class="tabularCustomHead">Contact HHS Accelerator</div>
<div class="tabularContainer"> 
	Thank you for contacting the HHS Accelerator Team. Your request has been submitted, and we will respond as soon as possible. 
</div>
<a href="javascript:void(0);" class="close-panel" onclick=" overlayHide();"></a>
<!-- Overlay Popup Ends -->
  
  
 
