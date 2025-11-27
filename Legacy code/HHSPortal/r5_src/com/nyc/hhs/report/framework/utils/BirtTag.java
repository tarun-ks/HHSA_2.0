package com.nyc.hhs.report.framework.utils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ReportBean;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.HHSPortalUtil;

/**
 * This is a utility class for Birt Tag
 */
@SuppressWarnings("serial")
public class BirtTag extends BodyTagSupport
{

	private String moXmlPath;
	private String moXmlPathId;
	private String reportId;
	private String transactionName;
	private String height;
	private String width;
	StringWriter moSw = new StringWriter();
	private static final LogInfo LOG_OBJECT = new LogInfo(BirtTag.class);

	@SuppressWarnings("unchecked")
	@Override
	public int doStartTag() throws JspException
	{
		JspWriter loWriterObj = pageContext.getOut();
		Map<String, Object> loDataSet = new HashMap<String, Object>();
		Map<String, Map<String, Object>> loConfigDetailMap = new HashMap<String, Map<String, Object>>();
		Map<String, Object> loReportRenderParam = new HashMap<String, Object>();
		ReportBean loReportBean = new ReportBean();
		String lsDataSource = null, lsQueryId = null, lsUserOrgType = null;
		List<ReportBean> loReportData = null;
		Channel loChannel = new Channel();
		Map<String, Object> loReportParams = null;
		boolean lbLoadingDashboard = false;
		try
		{
			LOG_OBJECT.Error("START  doStartTag ", CommonUtil.getCurrentTimeInMilliSec());
			lsUserOrgType = (String) ((HttpServletRequest) pageContext.getRequest()).getSession().getAttribute(
					ApplicationConstants.KEY_SESSION_ORG_TYPE);

            LOG_OBJECT.Debug("[BIRT Report] getContextPath::" + ((HttpServletRequest) pageContext.getRequest()).getSession().getServletContext().getContextPath() );
            LOG_OBJECT.Debug("[BIRT Report] protocol::" + pageContext.getRequest().getProtocol() );
            LOG_OBJECT.Debug("[BIRT Report] isSecure::" + pageContext.getRequest().isSecure() );

            
			// fetch configuration file
			loConfigDetailMap = HHSReportUtil.getReportConfigDetails(lsUserOrgType, reportId, null);
			loReportBean = (ReportBean) ((HttpServletRequest) pageContext.getRequest()).getSession().getAttribute(
					HHSR5Constants.REPORT_BEAN_LIST);
			loReportRenderParam = loConfigDetailMap.get(HHSR5Constants.RENDER_DETAILS);
			lsDataSource = (String) loReportRenderParam.get(HHSR5Constants.DATA_SOURCE);
			if (null != lsDataSource && lsDataSource.equalsIgnoreCase(HHSR5Constants.SCRIPTED))
			{
				lsQueryId = (String) loConfigDetailMap.get(HHSR5Constants.RENDER_DETAILS).get(HHSR5Constants.SQL_ID);
				if (loReportBean == null)
				{
					loReportBean = new ReportBean();
				}
				loReportBean.setDataGrid(false);
				loReportBean.setSqlId(lsQueryId);
				loChannel.setData(HHSR5Constants.REPORT_BEAN, loReportBean);
				TransactionManager.executeTransaction(loChannel, HHSR5Constants.GET_REPORT_DATA,
						HHSR5Constants.TRANSACTION_ELEMENT_R5);
				loReportData = (List<ReportBean>) loChannel.getData(HHSR5Constants.LO_REPORT_LIST);
				Map<String, Object> loVariableDetails = loConfigDetailMap.get(HHSR5Constants.VARIABLE);
				for (Map.Entry<String, Object> entrySet : loVariableDetails.entrySet())
				{
					if (entrySet.getKey().equalsIgnoreCase(HHSR5Constants.REPORT_LIST))
					{
						loVariableDetails.put(entrySet.getKey(), loReportData);
					}
				}
				loConfigDetailMap.put(HHSR5Constants.VARIABLE, loVariableDetails);
			}
			loReportParams = loConfigDetailMap.get(HHSR5Constants.INPUT);
			if (loReportParams != null)
			{
				for (Map.Entry<String, Object> entrySet : loReportParams.entrySet())
				{
					loDataSet.put(
							entrySet.getKey(),
							HHSPortalUtil.parseQueryString((HttpServletRequest) pageContext.getRequest(),
									entrySet.getKey()));
				}
				loConfigDetailMap.put(HHSR5Constants.INPUT, loDataSet);
			}
			if (null == loReportBean.getReportId() || loReportBean.getReportId().isEmpty())
			{
				lbLoadingDashboard = true;
			}
			ReportHandler.renderReport((HttpServletRequest) pageContext.getRequest(), loConfigDetailMap, loWriterObj,
					loReportBean.getFyYear(), lbLoadingDashboard);
			LOG_OBJECT.Error("END  doStartTag ", CommonUtil.getCurrentTimeInMilliSec());
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception occured birt do start tag", aoExp);
		}
		return super.doStartTag();
	}

	public String getMoXmlPath()
	{
		return moXmlPath;
	}

	public void setMoXmlPath(String moXmlPath)
	{
		this.moXmlPath = moXmlPath;
	}

	public String getMoXmlPathId()
	{
		return moXmlPathId;
	}

	public void setMoXmlPathId(String moXmlPathId)
	{
		this.moXmlPathId = moXmlPathId;
	}

	public String getReportId()
	{
		return reportId;
	}

	public void setReportId(String reportId)
	{
		this.reportId = reportId;
	}

	public String getTransactionName()
	{
		return transactionName;
	}

	public void setTransactionName(String transactionName)
	{
		this.transactionName = transactionName;
	}

	public String getHeight()
	{
		return height;
	}

	public void setHeight(String height)
	{
		this.height = height;
	}

	public String getWidth()
	{
		return width;
	}

	public void setWidth(String width)
	{
		this.width = width;
	}

	public StringWriter getMoSw()
	{
		return moSw;
	}

	public void setMoSw(StringWriter moSw)
	{
		this.moSw = moSw;
	}
}