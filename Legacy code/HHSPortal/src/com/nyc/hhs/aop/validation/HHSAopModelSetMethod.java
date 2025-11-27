package com.nyc.hhs.aop.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.HHSUtil;


/**
 * This class is added for R9.5.0. Use AOP to centralize the common code with pointcut and annotation (model set method value validation).
 */
@Aspect
public abstract class HHSAopModelSetMethod {
	
private static final LogInfo LOG_OBJECT = new LogInfo(HHSAopModelSetMethod.class);

	
	
	@Pointcut
	abstract void validateSetMethodAop();
	
	@Around("validateSetMethodAop()") 
	public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		
		boolean foundHazardousChars = false;
		
		Object[] loArgumentArray = proceedingJoinPoint.getArgs();
		String methodName = proceedingJoinPoint.getSignature().getName();	
		Object obj=null; //input
		
		try{
			if(methodName.startsWith("set") && (loArgumentArray!=null && loArgumentArray.length>0)){			
				obj=loArgumentArray[0]; 			
		
				if (obj != null) {
					for (Pattern rcePattern : ApplicationConstants.RCE_PATTERNS) {
						Matcher lsMatcher = rcePattern.matcher(HHSUtil.removeSpace((""+obj)
								.toLowerCase()));
						if (lsMatcher.find()) {
							// do not log the remote code input value (because log4j
							// jndi lookup Vulnerability, etc)
							LOG_OBJECT
									.Error("The request containing rce hazardous chars"
											+ ",rcePattern:"
											+ rcePattern + ",methodName:" +methodName);
							
							throw new RuntimeException(
									"The request containing rce hazardous chars");							
							
						}
					}//for 						
				}				
			}	//if(loArgumentArray!=null && loArgumentArray.length>0)	
					
		}catch (Exception e){
			foundHazardousChars = true;
			LOG_OBJECT.Error("HHSAopModelSetMethod error message:"+e.getMessage());
		}
		
		if(foundHazardousChars){
			LOG_OBJECT.Error("HHSAopModelSetMethod (doAround) contains hazardous");			
			throw new RuntimeException("HHSAopModelSetMethod (doAround) contains hazardous");	
		}
		return proceedingJoinPoint.proceed();
	}
		
	

}
