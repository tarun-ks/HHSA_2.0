package com.nyc.hhs.aop.validation;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.HHSUtil;


/**
 * This class is added for R9.5.0. Use AOP to centralize the common code with pointcut and annotation (portlet request getParameter value validation).
 */
@Aspect
public abstract class HHSAopParameterValidation {
	
private static final LogInfo LOG_OBJECT = new LogInfo(HHSAopParameterValidation.class);

	
	@Pointcut
	abstract void validateParameterAop();
	
	@Around("validateParameterAop()") 
	public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		
		boolean foundHazardousChars = false;
		//should be String as it's for for getParameter method
		Object[] loArgumentArray = proceedingJoinPoint.getArgs();
		Object obj=null;
		Object loRet = null;
		try{
			if(loArgumentArray!=null && loArgumentArray.length==1 ){			
				obj=loArgumentArray[0]; 
				if("actionException".equalsIgnoreCase(""+obj)){
					return proceedingJoinPoint.proceed();
				}
				loRet = proceedingJoinPoint.proceed();
				String lsValue ="" +loRet;		
			
				if (lsValue != null && !"null".equalsIgnoreCase(lsValue) && lsValue.trim().length() > 0) {
					for (Pattern rcePattern : ApplicationConstants.RCE_PATTERNS) {
						Matcher lsMatcher = rcePattern.matcher(HHSUtil.removeSpace(lsValue
								.toLowerCase()));
						if (lsMatcher.find()) {
							// do not log the remote code input value (because log4j
							// jndi lookup Vulnerability, etc)
							LOG_OBJECT
									.Error("The request containing rce hazardous chars"
											+ ",rcePattern:"
											+ rcePattern + ",paramaterName:" +obj);
							
							throw new RuntimeException(
									"The request containing rce hazardous chars");
														
						}
					}						
				}
				
			}	//if(loArgumentArray!=null && loArgumentArray.length>0)				
			
		}catch (Exception e){
			foundHazardousChars = true;
			LOG_OBJECT.Error("HHSAopParameterValidation error message:"+e.getMessage());
		}
		
		if(foundHazardousChars){
			LOG_OBJECT.Error("HHSAopParameterValidation (doAround) contains hazardous");
			//throw new ApplicationException("HHSParameterValidation (doAround) contains");			
			loRet="";
			throw new ApplicationException("HHSAopParameterValidation (doAround) contains hazardous");	
		}
		return loRet;
	}
		
	
	
}
