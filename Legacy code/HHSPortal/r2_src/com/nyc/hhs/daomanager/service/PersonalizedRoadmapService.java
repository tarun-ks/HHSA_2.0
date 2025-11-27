package com.nyc.hhs.daomanager.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.DAOUtil;

/**
 * * <ul><li><b>This Class has been added in R4</b></li></ul>
 * This service is used to save and delete favourites from procurement roadmap
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.
 * 
 */
public class PersonalizedRoadmapService extends ServiceState
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(PersonalizedRoadmapService.class);

	/**
	 * This method saves the favorites on click of save updates to favorite
	 * button
	 * <ul>
	 * <li> Check if favorite ids exist</li>
	 * <li>Collect the data in a map and execute query
	 * "checkIfFavoriteExists" to check if the pool title already exist in DB</li>
	 * <li>If it doesnt exist, execute query "saveFavorites" to insert the
	 * data in DB</li>
	 * </ul>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asFavoriteIds - favorite ids
	 * @param asUserId - User Id of logged in user
	 * @param asOrganizationId - Organization Id of logged in user
	 * @return flag stating save is successful or not
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean saveFavorites(SqlSession aoMybatisSession, String asFavoriteIds, String asUserId,
			String asOrganizationId) throws ApplicationException
	{
		boolean lbSaveSuccessFull = false;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			if (asFavoriteIds != null && !asFavoriteIds.isEmpty())
			{
				String loFavoriteIds[] = asFavoriteIds.split(HHSConstants.COMMA);
				loContextDataMap.put(HHSConstants.USER_ID, asUserId);
				loContextDataMap.put(HHSConstants.ORGANIZATION_ID, asOrganizationId);
				for (String lsFavoriteId : loFavoriteIds)
				{
					loContextDataMap.put(HHSConstants.PROCUREMENT_ID, lsFavoriteId);
					int liFavoriteExists = (Integer) DAOUtil.masterDAO(aoMybatisSession, loContextDataMap,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.CHECK_IF_FAV_EXISTS,
							HHSConstants.JAVA_UTIL_MAP);
					if (liFavoriteExists == 0)
					{
						DAOUtil.masterDAO(aoMybatisSession, loContextDataMap,
								HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.SAVE_FAVORITES,
								HHSConstants.JAVA_UTIL_MAP);
					}
				}
				lbSaveSuccessFull = true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while Saving favorites:" + asFavoriteIds);
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while Saving favorites:" + asFavoriteIds, loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while Saving favorites::" + asFavoriteIds, loEx);
			setMoState("Error while Saving favorites");
			throw new ApplicationException("Error while Saving favorites:" + asFavoriteIds, loEx);
		}
		return lbSaveSuccessFull;
	}

	/**
	 * This method deletes the favorites on click of save updates to favorite
	 * button
	 * <ul>
	 * <li>Check if non favorite ids exist</li>
	 * <li>Collect the data in a map and execute query "deleteFavorites"</li>
	 * </ul>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asNonFavoriteIds - Non favorite ids
	 * @param asUserId - User Id of logged in user
	 * @param asOrganizationId - Organization Id of logged in user
	 * @param abSaveSuccessFull - Update successful flag
	 * @return flag stating delete is successful or not
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean deleteFavorites(SqlSession aoMybatisSession, String asNonFavoriteIds, String asUserId,
			String asOrganizationId, Boolean abSaveSuccessFull) throws ApplicationException
	{
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			if (asNonFavoriteIds != null && !asNonFavoriteIds.isEmpty())
			{
				String loFavoriteIds[] = asNonFavoriteIds.split(HHSConstants.COMMA);
				loContextDataMap.put(HHSConstants.USER_ID, asUserId);
				loContextDataMap.put(HHSConstants.ORGANIZATION_ID, asOrganizationId);
				loContextDataMap.put(HHSConstants.PROCUREMENT_ID_LIST, Arrays.asList(loFavoriteIds));
				DAOUtil.masterDAO(aoMybatisSession, loContextDataMap, HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
						HHSConstants.DELETE_FAVORITES, HHSConstants.JAVA_UTIL_MAP);
				abSaveSuccessFull = true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while deleting favorites:" + asNonFavoriteIds);
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while deleting favorites:" + asNonFavoriteIds, loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while deleting favorites::" + asNonFavoriteIds, loEx);
			setMoState("Error while deleting favorites");
			throw new ApplicationException("Error while deleting favorites:" + asNonFavoriteIds, loEx);
		}
		return abSaveSuccessFull;
	}
}