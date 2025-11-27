package com.nyc.hhs.aop.access;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.web.portlet.ModelAndView;

import com.nyc.hhs.aop.access.handlers.BaseAccessHandler;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.AutoSaveBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.rule.Rule;
import com.nyc.hhs.sessionListener.SessionListener;
import com.nyc.hhs.util.XMLUtil;

@Aspect
public abstract class AccessAspect
{
	/**
	 * Log object to record all logs
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(AccessAspect.class);

	@Pointcut
	abstract void controllerMethods();

	@Pointcut
	abstract void controllerRenderMethods();

	@Pointcut
	abstract void resourceSolicitaionMethods();

	@Pointcut
	abstract void resourceFinancialMethods();

	@Pointcut
	abstract void resourceDocumentMethods();

	@Pointcut
	abstract void deleteInvoice();

	@Pointcut
	abstract void actionWithdrawInvoice();

	// Added for autoSave
	@Pointcut
	abstract void controllerActionMethods();

	// End

	/**
	 * This method is point cut to all render method of all controllers and will
	 * be responsible for removing all locks taken by user
	 * <ul>
	 * <li>1. get the object returned by render method</li>
	 * <li>2. get request from render method arguments</li>
	 * <li>3. invoke removeUser method to remove all locks taken by user</li>
	 * </ul>
	 * 
	 * @param aoPrecedingJointPoint - Proceeding join point object
	 * @return object been returned by method on which point cut is applied
	 * @throws Throwable - in case of any exception occurs
	 */
	@Around("controllerRenderMethods()")
	public Object releaseAccess(ProceedingJoinPoint aoPrecedingJointPoint) throws Throwable
	{
		LOG_OBJECT.Info("Start releaseAccess method of AccessAspect Class");
		Object loRet = aoPrecedingJointPoint.proceed();
		Object[] loArgumentArray = aoPrecedingJointPoint.getArgs();
		PortletRequest loRequest = null;
		if (loArgumentArray != null)
		{
			for (Object loArgObject : loArgumentArray)
			{
				if (loArgObject instanceof PortletRequest)
				{
					loRequest = (PortletRequest) loArgObject;
				}
			}
		}
		if (loRequest == null)
		{
			throw new ApplicationException(
					"Invalid controller method. Controller Render methods must have request parameter::: Class : "
							+ aoPrecedingJointPoint.getSignature().getDeclaringTypeName() + " : Method : "
							+ aoPrecedingJointPoint.getSignature().getName());
		}
		HttpSession loHttpSession = ((HttpServletRequest) loRequest.getAttribute(HHSConstants.JAVAX_SERVLET_REQUEST))
				.getSession();
		SessionListener.removeUser(loHttpSession);
		LOG_OBJECT.Info("End releaseAccess method of AccessAspect Class");
		return loRet;
	}

	/**
	 * This method is point cut to all render method of all controllers and will
	 * be responsible for getting the handler and locking a screen
	 * <ul>
	 * <li>1. get the object returned by render method</li>
	 * <li>2. get request from render method arguments</li>
	 * <li>3. get the handler corresponding to jsp and controller using
	 * "getHandler" method</li>
	 * <li>4. if handler is found generate unique id using "generateId" method
	 * of handler</li>
	 * <li>5. invoke method to check and lock a screen</li>
	 * </ul>
	 * 
	 * @param aoPrecedingJointPoint - Proceeding join point object
	 * @return object been returned by method on which point cut is applied
	 * @throws Throwable - in case of any exception occurs
	 */
	@SuppressWarnings("unchecked")
	@Around("controllerMethods() || resourceSolicitaionMethods() || resourceFinancialMethods() || resourceDocumentMethods() || deleteInvoice() || actionWithdrawInvoice()")
	public Object controlAccess(ProceedingJoinPoint aoPrecedingJointPoint) throws Throwable
	{
		LOG_OBJECT.Info("Start controleAccess method of AccessAspect Class");
		Object loRet = aoPrecedingJointPoint.proceed();
		Object[] loArgumentArray = aoPrecedingJointPoint.getArgs();
		PortletRequest loRequest = null;
		if (loArgumentArray != null)
		{
			for (Object loArgObject : loArgumentArray)
			{
				if (loArgObject instanceof PortletRequest)
				{
					loRequest = (PortletRequest) loArgObject;
				}
			}
		}
		if (loRequest == null)
		{
			throw new ApplicationException(
					"Invalid controller method. Controller Render methods must have request parameter::: Class : "
							+ aoPrecedingJointPoint.getSignature().getDeclaringTypeName() + " : Method : "
							+ aoPrecedingJointPoint.getSignature().getName());
		}
		if (loRequest.getAttribute(HHSConstants.HIDE_EXIT_PROCUREMENT) == null
				|| !((Boolean) loRequest.getAttribute(HHSConstants.HIDE_EXIT_PROCUREMENT)) && loRet != null)
		{
			Map<String, Object> loMap = getHandler(loRet, aoPrecedingJointPoint.getSignature().getDeclaringTypeName(),
					loRequest);
			if (loMap != null && loMap.get(HHSConstants.HANDLER_INSTANCE) != null)
			{
				String lsGeneratedId = ((BaseAccessHandler) loMap.get(HHSConstants.HANDLER_INSTANCE)).generateId(loRet,
						loRequest);
				if (lsGeneratedId != null && !lsGeneratedId.isEmpty())
				{
					((BaseAccessHandler) loMap.get(HHSConstants.HANDLER_INSTANCE)).checkAndAddLock(lsGeneratedId,
							(Boolean) loMap.get(HHSConstants.RULE_FLAG), loRequest);
				}
			}
		}

		// Added for fetching results form AutoSave R5

		String loUserid = (String) loRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		String loViewName = (String) loRequest.getPortletSession().getAttribute(HHSR5Constants.AUTO_SAVE_JSP_NAME,
				PortletSession.APPLICATION_SCOPE);
		String loEntityId = (String) loRequest.getPortletSession().getAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID,
				PortletSession.APPLICATION_SCOPE);
		String loEntityName = (String) loRequest.getPortletSession().getAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME,
				PortletSession.APPLICATION_SCOPE);
		if (loRet instanceof ModelAndView && null != loEntityId)
		{
			AutoSaveBean loAutoSaveBean = new AutoSaveBean();
			loAutoSaveBean.setJspName(loViewName);
			loAutoSaveBean.setUserId(loUserid);
			loAutoSaveBean.setEntityId(loEntityId);
			loAutoSaveBean.setEntityName(loEntityName);
			Channel loChannel = new Channel();

			loChannel.setData("autoSavebean", loAutoSaveBean);
			HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.AUTOSAVE_FETCH_TRANSACTION,
					HHSR5Constants.TRANSACTION_ELEMENT_R5);
			List<AutoSaveBean> loAutoSaveList = (List<AutoSaveBean>) loChannel
					.getData(HHSR5Constants.POPULATED_BEAN_LIST);
			loRequest.getPortletSession().setAttribute(HHSR5Constants.AUTO_SAVE_LIST, loAutoSaveList,
					PortletSession.APPLICATION_SCOPE);
		}
		// End
		LOG_OBJECT.Info("End controleAccess method of AccessAspect Class");
		return loRet;
	}

	/**
	 * This method gets the handler for a combination of jsp and controller
	 * <ul>
	 * <li>1. Get jsp name from returned object of pointcut method</li>
	 * <li>2. Search for current screen in child of previous screen, if exist do
	 * nothing else search for current screen as peer</li>
	 * <li>3. if current screen is found get handler, else remove current user
	 * from lock cache</li>
	 * <li>4. if no previous screen exist, perform step 3</li>
	 * </ul>
	 * 
	 * @param aoRet - Object returned by AOP
	 * @param asControllerName - Controller name been accessed by AOP
	 * @param aoRequest - Portlet Request
	 * @return Handler class object
	 * @throws ApplicationException - in case any exception occurs
	 */
	private Map<String, Object> getHandler(Object aoRet, String asControllerName, PortletRequest aoRequest)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Start getHandler method of AccessAspect Class");
		Element loAccessAspectElt = null;
		String lsXpath = null;
		String lsNextJSP = null;
		Map<String, Object> loMap = null;
		PortletSession loSession = aoRequest.getPortletSession();
		if (aoRet instanceof String)
		{
			lsNextJSP = (String) aoRet;
		}
		else if (aoRet instanceof ModelAndView)
		{
			lsNextJSP = ((ModelAndView) aoRet).getViewName();
		}
		if (lsNextJSP != null
				&& (lsNextJSP.contains(HHSConstants.FORWARD_SLASH) || lsNextJSP.contains(HHSConstants.STRING_BACKSLASH)))
		{
			lsNextJSP = lsNextJSP.replace(HHSConstants.STRING_BACKSLASH, HHSConstants.FORWARD_SLASH);
			lsNextJSP = lsNextJSP.substring(lsNextJSP.lastIndexOf(HHSConstants.FORWARD_SLASH) + 1);
		}
		try
		{
			String lsLastJSP = (String) loSession.getAttribute(HHSConstants.LAST_JSP_ACCESSED_KEY,
					PortletSession.APPLICATION_SCOPE);
			String lsLastController = (String) loSession.getAttribute(HHSConstants.LAST_CONTROLLER_ACCESSED_KEY,
					PortletSession.APPLICATION_SCOPE);
			Document loAccessAspectDom = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSConstants.CONCURRENCY_CONFIG_CACHE_KEY);
			if (lsLastJSP != null && !lsLastJSP.isEmpty() && lsLastController != null && !lsLastController.isEmpty())
			{
				lsXpath = "//peers/peer[(@jspid=\"" + lsLastJSP + "\" and @controller=\"" + lsLastController
						+ "\")]/child[(@jspid=\"" + lsNextJSP + "\" and @controller=\"" + asControllerName + "\")]";
				loAccessAspectElt = XMLUtil.getElement(lsXpath, loAccessAspectDom);
				if (loAccessAspectElt == null)
				{// check is screen to be
					// accessed is a peer
					loMap = getAccessAspectElement(asControllerName, lsNextJSP, aoRequest, loAccessAspectDom);
				}
			}
			else
			{
				loMap = getAccessAspectElement(asControllerName, lsNextJSP, aoRequest, loAccessAspectDom);
			}
		}
		catch (ApplicationException aoAe)
		{
			LOG_OBJECT.Error("Error occurred while getting handler for :: " + lsNextJSP + " controller :: "
					+ asControllerName, aoAe);
			throw aoAe;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while getting handler for :: " + lsNextJSP + " controller :: "
					+ asControllerName, aoEx);
			throw new ApplicationException("Error occured while fetching factory for Access Handler", aoEx);
		}
		if (loMap != null && loMap.get(HHSConstants.HANDLER_INSTANCE) != null)
		{
			loSession.setAttribute(HHSConstants.LAST_JSP_ACCESSED_KEY, lsNextJSP, PortletSession.APPLICATION_SCOPE);
			loSession.setAttribute(HHSConstants.LAST_CONTROLLER_ACCESSED_KEY, asControllerName,
					PortletSession.APPLICATION_SCOPE);
		}
		LOG_OBJECT.Info("End getHandler method of AccessAspect Class");
		// Added in R5
		loSession.setAttribute(HHSR5Constants.AUTO_SAVE_JSP_NAME, lsNextJSP, PortletSession.APPLICATION_SCOPE);
		loSession.removeAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID);
		loSession.removeAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME);
		// R5 ends
		return loMap;
	}

	/**
	 * This method get access aspect element handler
	 * <ul>
	 * <li>1. Get access aspect element</li>
	 * <li>2. Check if a rule exist for aspect element</li>
	 * <li>3. If rule exist, run the rule and get output</li>
	 * <li>4. if rule passes get the handler instance</li>
	 * </ul>
	 * 
	 * @param asControllerName - controller name
	 * @param asNextJSP - next jsp name
	 * @param aoRequest - Portlet Request
	 * @param aoAccessAspectDom - Concurrency conguration dom
	 * @return handler for selected jsp and controller
	 * @throws ApplicationException - in case any exception occurs
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, Object> getAccessAspectElement(String asControllerName, String asNextJSP,
			PortletRequest aoRequest, Document aoAccessAspectDom) throws ApplicationException
	{
		Map<String, Object> loMap = new HashMap<String, Object>();
		BaseAccessHandler loHandler = null;
		boolean lbIsRulePass = true;
		try
		{
			HttpSession loHttpSession = ((HttpServletRequest) aoRequest
					.getAttribute(HHSConstants.JAVAX_SERVLET_REQUEST)).getSession();
			String lsXpath = "//peers/peer[(@jspid=\"" + asNextJSP + "\" and @controller=\"" + asControllerName
					+ "\")]";
			Element loAccessAspectElt = XMLUtil.getElement(lsXpath, aoAccessAspectDom);
			if (loAccessAspectElt != null)
			{
				String lsRuleId = loAccessAspectElt.getAttributeValue(HHSConstants.RULE);
				String lsNegateRule = loAccessAspectElt.getAttributeValue(HHSConstants.NEGATE_RULE);
				if (lsRuleId != null)
				{
					Channel loChannel = (Channel) aoRequest.getAttribute(HHSConstants.CHANNEL_ACCESS);
					if (loChannel != null)
					{
						lbIsRulePass = Boolean.valueOf((String) Rule.evaluateRule(lsRuleId, loChannel));
						if (lsNegateRule != null && lsNegateRule.equalsIgnoreCase(HHSConstants.TRUE))
						{
							lbIsRulePass = !lbIsRulePass;
						}
					}
					else
					{
						lbIsRulePass = false;
					}
				}
				Class loClass = Class.forName(loAccessAspectElt.getAttributeValue(HHSConstants.HANDLER_NAME));
				loHandler = (BaseAccessHandler) loClass.newInstance();
			}
			else
			{
				SessionListener.removeUser(loHttpSession);
			}
		}
		catch (ApplicationException aoAe)
		{
			LOG_OBJECT.Error("Error occurred while getting handler for :: " + asNextJSP + " controller :: "
					+ asControllerName, aoAe);
			throw aoAe;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while getting handler for :: " + asNextJSP + " controller :: "
					+ asControllerName, aoEx);
			throw new ApplicationException("Error occured while fetching factory for Access Handler", aoEx);
		}
		loMap.put(HHSConstants.HANDLER_INSTANCE, loHandler);
		loMap.put(HHSConstants.RULE_FLAG, lbIsRulePass);
		return loMap;
	}

	// Added for autoSave R5

	/**
	 * This method set Property Value and was added for auto save R5
	 * <ul>
	 * <li>1. Get Bean Info</li>
	 * <li>2. Check if pd.getName equals property Name</li>
	 * <li>3. If rule exist, getWriteMethod</li>
	 * </ul>
	 * 
	 * @param javaBean - controller name
	 * @param propertyName - name of property
	 * @param propertyValue - value of property
	 * @throws ApplicationException - in case any exception occurs
	 */
	public void setPropertyValue(Object javaBean, String propertyName, Object propertyValue)
			throws ApplicationException
	{
		try
		{
			BeanInfo bi = Introspector.getBeanInfo(javaBean.getClass());
			PropertyDescriptor pds[] = bi.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds)
			{
				if (pd.getName().equals(propertyName))
				{
					Method setter = pd.getWriteMethod();
					if (setter != null)
					{
						setter.invoke(javaBean, new Object[]
						{ propertyValue });
					}
				}
			}
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while executing AccessAspect.setPropertyValue() ", aoEx);
			throw new ApplicationException("Error occured while fetching factory for Access Handler", aoEx);
		}
	}

	/**
	 * This method set access Thread Local
	 * <ul>
	 * <li>1. Get Bean Info</li>
	 * <li>2. Check if loArgumentArray have value</li>
	 * <li>3. If rule exist, loRequest stores loArgObject</li>
	 * <li>4. throw ApplicationException if rule fails</li>
	 * </ul>
	 * 
	 * @param aoPrecedingJointPoint - ProceedingJoinPoint
	 * @return loRet - Object
	 * @throws Throwable - in case any exception occurs
	 */
	@Around("controllerActionMethods()")
	public Object accessThreadLocal(ProceedingJoinPoint aoPrecedingJointPoint) throws Throwable
	{
		LOG_OBJECT.Info("Start accessThreadLocal method of AccessAspect Class");
		Object loRet = aoPrecedingJointPoint.proceed();
		Object[] loArgumentArray = aoPrecedingJointPoint.getArgs();
		PortletRequest loRequest = null;
		if (loArgumentArray != null)
		{
			for (Object loArgObject : loArgumentArray)
			{
				if (loArgObject instanceof PortletRequest)
				{
					loRequest = (PortletRequest) loArgObject;
				}
			}
		}
		if (loRequest == null)
		{
			throw new ApplicationException(
					"Invalid controller method. Controller Render methods must have request parameter::: Class : "
							+ aoPrecedingJointPoint.getSignature().getDeclaringTypeName() + " : Method : "
							+ aoPrecedingJointPoint.getSignature().getName());
		}

		return loRet;
	}
}