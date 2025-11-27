package com.nyc.hhs.aop.token;



import java.io.PrintWriter;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.annotation.Around;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.portlet.context.PortletRequestAttributes;
import com.nyc.hhs.annotation.HHSExtToken;
import com.nyc.hhs.annotation.HHSTokenValidator;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.HHSTokenUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class is added for R8.6.0. Use AOP to centralize the common code with pointcut and annotation.
 */

@Aspect
public abstract class HHSAopToken {
	private static final LogInfo LOG_OBJECT = new LogInfo(HHSAopToken.class);
	
	@Pointcut
	abstract void tokenAop();

	@Before("tokenAop()")//look for annotation HHSExtToken
	public void before(JoinPoint point) {
		MethodSignature signature = (MethodSignature) point.getSignature();
		if( signature.getMethod().isAnnotationPresent(HHSExtToken.class)){	
			HttpServletRequest request = HHSTokenUtil.getRequest();
			if(request!=null && request.getSession()!=null){
				if(request.getSession().getAttribute(HHSTokenUtil.getTokenKey())==null){
					request.getSession().setAttribute(HHSTokenUtil.getTokenKey(), HHSTokenUtil.genTokenValue());
				}
			}
			
		}
	}
	@Around("tokenAop()") //look for annotation HHSTokenValidator
	public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
		String tokenParameter = null;
		String tokenSession = null;
		Object proceed =null;
		if( signature.getMethod().isAnnotationPresent(HHSTokenValidator.class)){
			HttpServletRequest request = HHSTokenUtil.getRequest();
			
			if(request!=null && request.getParameterMap().containsKey(HHSTokenUtil.getTokenKey())){
				tokenParameter  = request.getParameter(HHSTokenUtil.getTokenKey());
				
				if(request.getSession()!=null){
					tokenSession =(String) request.getSession().getAttribute(HHSTokenUtil.getTokenKey());
					if(StringUtils.equals(tokenParameter, tokenSession)){
						request.getSession().removeAttribute(HHSTokenUtil.getTokenKey());
						proceed = proceedingJoinPoint.proceed();					
					}
					//else -- it's multiple submission, stop the request and show message
					else {						
						//String asDebugMessage="multiple submission";
						String asDebugMessage = HHSConstants.PAGE_ERROR + HHSConstants.COLON + PropertyLoader.getProperty(
								HHSConstants.ERROR_MESSAGES_PROPERTY_FILE, HHSConstants.DUPLICATE_SUBMISSION);
			
						LOG_OBJECT.Debug("HHSAopToken:doAround method, message : " + asDebugMessage);
						response(asDebugMessage);
						return null;
					}
				}//if(request.getSession()!=null)
			}//if(request!=null)
	    }//if( signature.getMethod().isAnnotationPresent(HHSTokenValidator.class))
		else{
			proceed = proceedingJoinPoint.proceed();			
		}
		return proceed;
	}
	
	public void response(String msg) {
		PrintWriter writer = null;
		try {
			RequestAttributes requestAttributes = RequestContextHolder
					.getRequestAttributes();
			PortletRequestAttributes portletRequestAttributes = (PortletRequestAttributes) requestAttributes;
			PortletRequest portletRequest = portletRequestAttributes.getRequest();
		
			HttpServletResponse response=
			    (HttpServletResponse)portletRequest.getAttribute(
			    		HHSConstants.JAVAX_SERVLET_RESPONSE);		
			writer = response.getWriter();		
			writer.println(msg);
		} catch (Exception e) {
			LOG_OBJECT.Error("Error on HHSAopToken response:" +e.getMessage(),e);
		} finally {
			if(writer!=null){
				writer.flush();
				writer.close();
			}
		}

		}
	

}
