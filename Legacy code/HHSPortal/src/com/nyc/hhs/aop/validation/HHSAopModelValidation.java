package com.nyc.hhs.aop.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springmodules.validation.bean.BeanValidator;
import org.springmodules.validation.bean.conf.loader.annotation.AnnotationBeanValidationConfigurationLoader;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.CommonUtil;

/**
 * This class is added for R9.3.0. Use AOP to centralize the common code with pointcut and annotation (data model validation).
 */
@Aspect
public abstract class HHSAopModelValidation {
	
private static final LogInfo LOG_OBJECT = new LogInfo(HHSAopModelValidation.class);

	private static BeanValidator validator;
	private Map<String, String> tempMap=TempBean.getMap();
	private static HashMap<String, String> loApplicationSettingMap;
	private static boolean modelValidationFlag= true; //default is true if not set in the APPLICATION_SETTINGS table, model validation can be turned off through the value in the APPLICATION_SETTINGS table (COMPONENT_NAME:ModelValidation, SETTINGS_NAME: ValidationFlag, SETTINGS_VALUE: 'false')

	static {
		
		validator = new BeanValidator(
		        new AnnotationBeanValidationConfigurationLoader());
		
		try{
			loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb		
				.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			if(loApplicationSettingMap!=null){
				//modelValidationFalg = (Boolean) loApplicationSettingMap.get(ApplicationConstants.MODEL_VALIDATION_APP_COMPONENT_NAME +  HHSConstants.UNDERSCORE + ApplicationConstants.MODEL_VALIDATION_APP_SETTING_NAME);
			    String modelValidationFlagStr = loApplicationSettingMap.get(ApplicationConstants.MODEL_VALIDATION_APP_COMPONENT_NAME +  HHSConstants.UNDERSCORE + ApplicationConstants.MODEL_VALIDATION_APP_SETTING_NAME);
			    if(modelValidationFlagStr!=null && (modelValidationFlagStr.equalsIgnoreCase("true") || modelValidationFlagStr.equalsIgnoreCase("false")) ){
			    	modelValidationFlag =Boolean.valueOf(modelValidationFlagStr);
			    	LOG_OBJECT.Info("HHSAopModelValidation modelValidationFlag from APPLICATION_SETTINGS table(when it set to 'true' or 'false'):" +modelValidationFlag);
			    }
			}			
		}catch(Exception e){
			LOG_OBJECT.Error("HHSAopModelValidation get ApplicationSettingMap error message:"+e.getMessage());
		}
		LOG_OBJECT.Info("HHSAopModelValidation modelValidationFlag:" +modelValidationFlag);
	}
	
	@Pointcut
	abstract void validateAop();
	
	@Around("validateAop()") 
	public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		//if modelValidationFlag is false, not to do model Validation
		if(!modelValidationFlag){
			return proceedingJoinPoint.proceed();
		}
		boolean hasErrors = false;
		Object[] loArgumentArray = proceedingJoinPoint.getArgs();		
		String methodName = proceedingJoinPoint.getSignature().getName();	
		Object obj=null;// the HashMap or models obj
		TempBean tempBean = new TempBean(); //HashMap argument in mapper to convert to TempBean for validation
		
		//String className=proceedingJoinPoint.getSignature().getDeclaringTypeName();		
		try{
			if(loArgumentArray!=null && loArgumentArray.length>0){			
				obj=loArgumentArray[0]; // see the aop.xml, only for HashMap or models ( under package com.nyc.hhs.model) in mybatis mapper service input arg  
				//for HashMap obj
				if (obj instanceof HashMap<?, ?> && setData(tempBean, (HashMap)obj)){					
						hasErrors = validateInput(tempBean);					
				}
				//for mapper models
				else{
					hasErrors = validateInput(obj);	
				}
			}		
			LOG_OBJECT.Debug("HHSAopModelValidation hasErrors(true/false):" +hasErrors + ",methodName:" +methodName);
		}
		catch (Exception e){
			LOG_OBJECT.Error("HHSAopModelValidation error message:"+e.getMessage());
		}
		
		if(!hasErrors){
			return proceedingJoinPoint.proceed();
		}else{
			LOG_OBJECT.Error("HHSAopModelValidation (doAround) contains error:"+hasErrors + ",methodName:" +methodName);
			throw new ApplicationException("HHSAopModelValidation (doAround) contains error:"+hasErrors + ",methodName:" +methodName);	
		}
		
	}
		
	
	/**
	 * @param t the bean object	
	 * @return
	 */
	private static <T> boolean validateInput(T t){		
		boolean hasErrors=false; //default to false
		if(t==null){
			return hasErrors;
		}
		String objName=t.getClass().getName();
		DataBinder binder = new DataBinder(t);
		binder.setValidator(validator);			

		// get BindingResult that will hold any validation errors
		BindingResult results = binder.getBindingResult();
		try{
			 LOG_OBJECT.Debug("HHSAopModelValidation validateInput method, objName : " + objName);		
			 validator.validate(t, results);
			 if (results!=null && results.hasErrors())
				{
				 List<ObjectError> allErrors = results.getAllErrors();
				  for (ObjectError fieldError : allErrors) {
					  LOG_OBJECT.Error("HHSAopModelValidation error, validateInput info:"+ CommonUtil.maskPassword(fieldError.toString()));
				    }
				  hasErrors = true;
				}
			 else{
				 hasErrors = false;				
			 }
			}
			catch (ApplicationException aoException)
			{
				LOG_OBJECT.Error("ApplicationException occurred in validateInput ", aoException);
			}
		 return hasErrors;
		 
	}
	/**
	 * @param tempBean
	 * @param fields
	 * @return boolean has the common field or not to set to TempBean
	 */
	private boolean setData(TempBean tempBean, HashMap<String, String> fields) {
	   String name="";// the name of the field from the mapper input argument : hashMap
	   boolean hasField=false;
	   if(fields==null || fields.size() ==0 ){
		   return false;
	   }
	   for(Entry<String, String> entry : fields.entrySet()) {
		    name = entry.getKey(); 
            if (name == null) {
                continue;
            }
		   try{	
			   String tempName=tempMap.get(name); //get the field name from TempBean(validation for common known fields)
			   if(tempName!=null && entry.getValue()!=null){
				   BeanUtils.setProperty(tempBean, tempName, entry.getValue()); // set the value to TempBean
				   hasField=true;
			   }
		   }catch(Exception e){
			   LOG_OBJECT.Error("setData error occurred : ", e);
		   }
		 
	   }
	   return hasField;
	}
	
	
		
}
