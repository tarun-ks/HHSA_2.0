<% 
  if (com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb.getInstance().getCacheObject(com.nyc.hhs.constants.HHSR5Constants.DICTIONARY_DATA)==null
		|| org.apache.commons.lang.StringUtils.isEmpty((String)com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb.getInstance().getCacheObject(com.nyc.hhs.constants.HHSR5Constants.DICTIONARY_DATA))
   )
 { 
	response.setHeader( "Cache-Control", "no-cache" );
	response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	response.setHeader("Expires", "0"); // Proxies.
	out.print("");
 }else{	   
	response.setHeader("Cache-Control","public");
	response.setHeader("Cache-Control","max-age=432000");//5days (60sec * 60min * 24hours * 5days)
	response.setHeader("Expires", "432000"); 
	String data = ((String)com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb.getInstance().getCacheObject(com.nyc.hhs.constants.HHSR5Constants.DICTIONARY_DATA)).trim();
	out.println(data);
 }  
%>


	
