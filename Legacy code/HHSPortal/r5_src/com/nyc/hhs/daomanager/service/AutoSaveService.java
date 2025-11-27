package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AutoSaveBean;
import com.nyc.hhs.model.TempBean;
import com.nyc.hhs.util.DAOUtil;

/**
 * This class is added for release 5 This service class will get the method
 * calls through transaction layer. Execute queries by calling mapper and return
 * query output back to controller. If any error exists, wrap the exception into
 * Application Exception and throw it to controller.
 */
public class AutoSaveService extends ServiceState
{
	/**
	 * Logger object for AutoSaveService class.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(AutoSaveService.class);

	/**
	 * <p>
	 * This method added as a part of release 5. This method update the
	 * text-areas content corresponding to a jsp
	 * <ul>
	 * <li>AutoSaveBean input parameters</li>
	 * <li>Update DB using<b>updateAutoSave</b></li>
	 * <li>insert in DB using<b>insertAutoSave</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession Sql session object
	 * @param aoAutoSaveBean count of auto save object
	 * @return loCount 
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Integer updateAutoSave(SqlSession aoMybatisSession, AutoSaveBean aoAutoSaveBean) throws ApplicationException
	{
		LOG_OBJECT.Info("Entering updateAutoSave");
		Integer loCount = null;
		List<TempBean> loList = aoAutoSaveBean.getTempBean();
		for (TempBean loListObj : loList)
		{
			aoAutoSaveBean.setTextareaName(loListObj.getName());
			aoAutoSaveBean.setTextareaValue(loListObj.getValue());
			try
			{
				if (StringUtils.isNotBlank(aoAutoSaveBean.getTextareaValue()))
				{
					loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoAutoSaveBean,
							HHSR5Constants.MAPPER_CLASS_AUTOSAVE_MAPPER, HHSR5Constants.UPDATE_AUTO_SAVE,
							HHSR5Constants.AUTO_SAVE_BEAN);
					if (null == loCount || loCount == 0)
					{
						loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoAutoSaveBean,
								HHSR5Constants.MAPPER_CLASS_AUTOSAVE_MAPPER, HHSR5Constants.INSERT_AUTO_SAVE,
								HHSR5Constants.AUTO_SAVE_BEAN);
					}
				}
				else
				{
					loCount = deleteAutoSave(aoMybatisSession, aoAutoSaveBean);
				}
			}
			catch (ApplicationException loAppEx)
			{
				setMoState("Error occurred while update Auto Save");
				LOG_OBJECT.Error("Error occurred while update Auto Save", loAppEx);
				throw loAppEx;
			}
		}

		return loCount;
	}

	/**
	 * This Method delete the AutoSave data
	 * @param aoMybatisSession Sql session object
	 * @param aoAutoSaveBean count of auto save object
	 * @return locount
	 * @throws ApplicationException when any exception occurred wrap it into application exception.
	 */
	private Integer deleteAutoSave(SqlSession aoMybatisSession, AutoSaveBean aoAutoSaveBean)
			throws ApplicationException
	{
		Integer loCount;
		loCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoAutoSaveBean,
				HHSR5Constants.MAPPER_CLASS_AUTOSAVE_MAPPER, HHSR5Constants.DELETE_AUTO_SAVE,
				HHSR5Constants.AUTO_SAVE_BEAN);
		return loCount;
	}

	/**
	 * <p>
	 * This method added as a part of release 5 This method fetches text-areas
	 * values corresponding to a jsp.
	 * <ul>
	 * <li>AutoSaveBean input parameters</li>
	 * <li>Fetch text-area Details for the screen landing into using
	 * <b>fetchAutoSave</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession Sql session object
	 * @param aoAutoSaveBean count of auto save object
	 * @return loPopulatedBeanList List of type AutoSaveBean
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	@SuppressWarnings("unchecked")
	public List<AutoSaveBean> fetchAutoSave(SqlSession aoMybatisSession, AutoSaveBean aoAutoSaveBean)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entering fetchAutoSave");
		List<AutoSaveBean> loPopulatedBeanList = new ArrayList<AutoSaveBean>();
		try
		{
			loPopulatedBeanList = (List<AutoSaveBean>) DAOUtil.masterDAO(aoMybatisSession, aoAutoSaveBean,
					HHSR5Constants.MAPPER_CLASS_AUTOSAVE_MAPPER, HHSR5Constants.GET_AUTO_SAVE_INFO,
					HHSR5Constants.AUTO_SAVE_BEAN);
			for (AutoSaveBean loAutoSaveBean : loPopulatedBeanList)
			{
				loAutoSaveBean.setTextareaValue(StringEscapeUtils.escapeJava(loAutoSaveBean.getTextareaValue()));
			}
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Error occurred while fetch Auto Save");
			LOG_OBJECT.Error("Error occurred while fetch Auto Save", loAppEx);
			throw loAppEx;
		}
		return loPopulatedBeanList;
	}

	/**
	 * <p>
	 * This method added as a part of release 5 This method deletes data from
	 * database.
	 * <ul>
	 * <li>AutoSaveBean input parameters</li>
	 * <li>Fetch text-area Details for the screen landing into using
	 * <b>deleteAutoSave</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession Sql session object
	 * @param asEntityId entity id string
	 * @param asEntityName name of entity
	 * @return integer loCount
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public Integer deleteFromAutoSave(SqlSession aoMybatisSession, String asEntityId, String asEntityName)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entering deleteFromAutoSave");
		Integer loCount = null;
		AutoSaveBean loAutoSaveBean = new AutoSaveBean();
		loAutoSaveBean.setEntityId(asEntityId);
		loAutoSaveBean.setEntityName(asEntityName);
		try
		{
			if (null != asEntityId)

			{
				loCount = deleteAutoSave(aoMybatisSession, loAutoSaveBean);
			}
		}
		catch (ApplicationException loAppEx)
		{
			setMoState("Error occurred while delete From AutoSave");
			LOG_OBJECT.Error("Error occurred while delete From AutoSave", loAppEx);
			throw loAppEx;
		}
		return loCount;
	}

}
