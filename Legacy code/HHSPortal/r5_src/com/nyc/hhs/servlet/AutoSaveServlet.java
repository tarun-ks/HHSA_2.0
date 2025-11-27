package com.nyc.hhs.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.AutoSaveBean;
import com.nyc.hhs.model.TempBean;
import com.nyc.hhs.util.CommonUtil;

/**
 * Servlet implementation class AutoSave
 */
public class AutoSaveServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static final LogInfo LOG_OBJECT = new LogInfo(AutoSaveServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AutoSaveServlet()
	{
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest aorequest, HttpServletResponse aoresponse) throws ServletException,
			IOException
	{
		this.doPost(aorequest, aoresponse);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest aorequest, HttpServletResponse aoresponse) throws ServletException,
			IOException
	{
		LOG_OBJECT.Info("Entering AutoSaveServlet: doPost");
		String lsUserId = (String) aorequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		String lsJspName = (String) aorequest.getSession().getAttribute(HHSR5Constants.AUTO_SAVE_JSP_NAME);
		String lsEntityId = (String) aorequest.getSession().getAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_ID);
		String lsEntityName = (String) aorequest.getSession().getAttribute(HHSR5Constants.AUTO_SAVE_ENTITY_NAME);
		String lsAutoSaveAction = aorequest.getParameter(HHSR5Constants.AUTO_SAVE_ACTION);
		Channel loChannel = new Channel();
		if (lsAutoSaveAction.equalsIgnoreCase(HHSR5Constants.UPDATE_AUTO_SAVE_DATA))
		{
			if (StringUtils.isNotBlank(lsEntityId))
			{
				LOG_OBJECT.Info("Entering AutoSaveServlet: update AutoSave Data");
				AutoSaveBean loAutoSaveBean = new AutoSaveBean();
				List<TempBean> loAutoSaveBeanList = new ArrayList<TempBean>();
				String lsUserOrgType = (String) aorequest.getSession().getAttribute(
						ApplicationConstants.KEY_SESSION_ORG_TYPE);
				Enumeration<String> parameterNames = aorequest.getParameterNames();
				while (parameterNames.hasMoreElements())
				{
					String lsKey = (String) parameterNames.nextElement();
					String lsVal = aorequest.getParameter(lsKey);
					if (!lsKey.equalsIgnoreCase(HHSR5Constants.AUTO_SAVE_ACTION)
							&& !lsVal.equalsIgnoreCase(HHSR5Constants.UPDATE_AUTO_SAVE_DATA))
					{
						TempBean loTempBean = new TempBean();
						loTempBean.setName(lsKey);
						loTempBean.setValue(lsVal);
						loAutoSaveBeanList.add(loTempBean);
					}
				}
				loAutoSaveBean.setTempBean(loAutoSaveBeanList);
				loAutoSaveBean.setEntityId(lsEntityId);
				loAutoSaveBean.setJspName(lsJspName);
				loAutoSaveBean.setUserId(lsUserId);
				loAutoSaveBean.setEntityName(lsEntityName);
				loAutoSaveBean.setOrgType(lsUserOrgType);
				loChannel.setData(HHSR5Constants.AUTO_SAVE_BEAN_SERVLET, loAutoSaveBean);
				try
				{
					HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.AUTOSAVE_TRANSACTION,
							HHSR5Constants.TRANSACTION_ELEMENT_R5);
					loChannel.getData(HHSConstants.COUNT);
				}
				catch (ApplicationException loApex)
				{
					LOG_OBJECT.Error("Error occurred AutoSaveServlet: doPost, while update the Data in AutoSave Table",
							loApex);
				}
			}
		}
		else if (lsAutoSaveAction.equalsIgnoreCase(HHSR5Constants.DELETE_AUTO_SAVE_DATA))
		{
			try
			{
				LOG_OBJECT.Info("Entering AutoSaveServlet: delete from AutoSave Data");
				CommonUtil.setChannelForAutoSaveData(loChannel, lsEntityId, lsEntityName);
				HHSTransactionManager.executeTransaction(loChannel, HHSR5Constants.DELETE_FROM_AUTO_SAVE,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
			}
			catch (ApplicationException loApex)
			{
				LOG_OBJECT.Error("Error occurred AutoSaveServlet: doPost, while delete from AutoSave Table", loApex);
			}
		}
	}
}