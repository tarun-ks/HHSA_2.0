package com.nyc.hhs.model;

import java.util.Date;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * This class is a bean which maintains agency settings module details.
 * 
 */

public class AgencySettingsBean
{

	// Agency_setting_main table mapping
	private int agencySettingMainId;
	@Length(max = 20)
	private String agencyId;
	@RegExp(value ="^\\d{0,22}")
	private int reviewProcessId;
	@RegExp(value ="^\\d{0,22}")
	private int levelOfReview;
	private int oldLevelOfReview;

	// Agency_setting_sub table mapping
	private int agencySettingSubId;
	private int levelId;
	private String levelReviewerId;
	private Date lastUpdateDate;
	private Date createdDate;
	private String createdByUserId;
	private Date modifiedDate;
	private String modifiedByUserId;
	// end

	private List<AgencyDetailsBean> allAgencyDetailsBeanList;
	private List<ReviewProcessBean> allReviewProcessBeanList;

	private List<CityUserDetailsBean> allAgencyUsersList;
	private List<CityUserDetailsBean> allLevel1UsersList;
	private List<CityUserDetailsBean> allLevel2UsersList;
	private List<CityUserDetailsBean> allLevel3UsersList;
	private List<CityUserDetailsBean> allLevel4UsersList;
	
	
	/*[Start] QC9701*/
	private List<ActionBean> actionMenuList;
	/*[End] QC9701*/
	
	// variable added as a part of release 3.8.0 enhancement 6534
	private String reviewLevelChangeInProgress;
	private String taskType;

	/*[Start] QC9701*/
	public List<ActionBean> getActionMenuList() {
		return actionMenuList;
	}

	public void setActionMenuList(List<ActionBean> actionMenuList) {
		this.actionMenuList = actionMenuList;
	}
	/*[End] QC9701*/

	/**
	 * @return the agencySettingMainId
	 */
	public int getAgencySettingMainId()
	{
		return agencySettingMainId;
	}

	/**
	 * @param agencySettingMainId the agencySettingMainId to set
	 */
	public void setAgencySettingMainId(int agencySettingMainId)
	{
		this.agencySettingMainId = agencySettingMainId;
	}

	/**
	 * @return the agencyId
	 */
	public String getAgencyId()
	{
		return agencyId;
	}

	/**
	 * @param agencyId the agencyId to set
	 */
	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	/**
	 * @return the reviewProcessId
	 */
	public int getReviewProcessId()
	{
		return reviewProcessId;
	}

	/**
	 * @param reviewProcessId the reviewProcessId to set
	 */
	public void setReviewProcessId(int reviewProcessId)
	{
		this.reviewProcessId = reviewProcessId;
	}

	/**
	 * @return the levelOfReview
	 */
	public int getLevelOfReview()
	{
		return levelOfReview;
	}

	/**
	 * @param levelOfReview the levelOfReview to set
	 */
	public void setLevelOfReview(int levelOfReview)
	{
		this.levelOfReview = levelOfReview;
	}

	/**
	 * @return the oldLevelOfReview
	 */
	public int getOldLevelOfReview()
	{
		return oldLevelOfReview;
	}

	/**
	 * @param oldLevelOfReview the oldLevelOfReview to set
	 */
	public void setOldLevelOfReview(int oldLevelOfReview)
	{
		this.oldLevelOfReview = oldLevelOfReview;
	}

	/**
	 * @return the agencySettingSubId
	 */
	public int getAgencySettingSubId()
	{
		return agencySettingSubId;
	}

	/**
	 * @param agencySettingSubId the agencySettingSubId to set
	 */
	public void setAgencySettingSubId(int agencySettingSubId)
	{
		this.agencySettingSubId = agencySettingSubId;
	}

	/**
	 * @return the levelId
	 */
	public int getLevelId()
	{
		return levelId;
	}

	/**
	 * @param levelId the levelId to set
	 */
	public void setLevelId(int levelId)
	{
		this.levelId = levelId;
	}

	/**
	 * @return the levelReviewerId
	 */
	public String getLevelReviewerId()
	{
		return levelReviewerId;
	}

	/**
	 * @param levelReviewerId the levelReviewerId to set
	 */
	public void setLevelReviewerId(String levelReviewerId)
	{
		this.levelReviewerId = levelReviewerId;
	}

	/**
	 * @return the lastUpdateDate
	 */
	public Date getLastUpdateDate()
	{
		return lastUpdateDate;
	}

	/**
	 * @param lastUpdateDate the lastUpdateDate to set
	 */
	public void setLastUpdateDate(Date lastUpdateDate)
	{
		this.lastUpdateDate = lastUpdateDate;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}

	/**
	 * @return the createdByUserId
	 */
	public String getCreatedByUserId()
	{
		return createdByUserId;
	}

	/**
	 * @param createdByUserId the createdByUserId to set
	 */
	public void setCreatedByUserId(String createdByUserId)
	{
		this.createdByUserId = createdByUserId;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate()
	{
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the modifiedByUserId
	 */
	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}

	/**
	 * @param modifiedByUserId the modifiedByUserId to set
	 */
	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
	}

	/**
	 * @return the allAgencyDetailsBeanList
	 */
	public List<AgencyDetailsBean> getAllAgencyDetailsBeanList()
	{
		return allAgencyDetailsBeanList;
	}

	/**
	 * @param allAgencyDetailsBeanList the allAgencyDetailsBeanList to set
	 */
	public void setAllAgencyDetailsBeanList(List<AgencyDetailsBean> allAgencyDetailsBeanList)
	{
		this.allAgencyDetailsBeanList = allAgencyDetailsBeanList;
	}

	/**
	 * @return the allReviewProcessBeanList
	 */
	public List<ReviewProcessBean> getAllReviewProcessBeanList()
	{
		return allReviewProcessBeanList;
	}

	/**
	 * @param allReviewProcessBeanList the allReviewProcessBeanList to set
	 */
	public void setAllReviewProcessBeanList(List<ReviewProcessBean> allReviewProcessBeanList)
	{
		this.allReviewProcessBeanList = allReviewProcessBeanList;
	}

	/**
	 * @return the allAgencyUsersList
	 */
	public List<CityUserDetailsBean> getAllAgencyUsersList()
	{
		return allAgencyUsersList;
	}

	/**
	 * @param allAgencyUsersList the allAgencyUsersList to set
	 */
	public void setAllAgencyUsersList(List<CityUserDetailsBean> allAgencyUsersList)
	{
		this.allAgencyUsersList = allAgencyUsersList;
	}

	/**
	 * @return the allLevel1UsersList
	 */
	public List<CityUserDetailsBean> getAllLevel1UsersList()
	{
		return allLevel1UsersList;
	}

	/**
	 * @param allLevel1UsersList the allLevel1UsersList to set
	 */
	public void setAllLevel1UsersList(List<CityUserDetailsBean> allLevel1UsersList)
	{
		this.allLevel1UsersList = allLevel1UsersList;
	}

	/**
	 * @return the allLevel2UsersList
	 */
	public List<CityUserDetailsBean> getAllLevel2UsersList()
	{
		return allLevel2UsersList;
	}

	/**
	 * @param allLevel2UsersList the allLevel2UsersList to set
	 */
	public void setAllLevel2UsersList(List<CityUserDetailsBean> allLevel2UsersList)
	{
		this.allLevel2UsersList = allLevel2UsersList;
	}

	/**
	 * @return the allLevel3UsersList
	 */
	public List<CityUserDetailsBean> getAllLevel3UsersList()
	{
		return allLevel3UsersList;
	}

	/**
	 * @param allLevel3UsersList the allLevel3UsersList to set
	 */
	public void setAllLevel3UsersList(List<CityUserDetailsBean> allLevel3UsersList)
	{
		this.allLevel3UsersList = allLevel3UsersList;
	}

	/**
	 * @return the allLevel4UsersList
	 */
	public List<CityUserDetailsBean> getAllLevel4UsersList()
	{
		return allLevel4UsersList;
	}

	/**
	 * @param allLevel4UsersList the allLevel4UsersList to set
	 */
	public void setAllLevel4UsersList(List<CityUserDetailsBean> allLevel4UsersList)
	{
		this.allLevel4UsersList = allLevel4UsersList;
	}

	@Override
	public String toString()
	{
		return "AgencySettingsBean [agencySettingMainId=" + agencySettingMainId + ", agencyId=" + agencyId
				+ ", reviewProcessId=" + reviewProcessId + ", levelOfReview=" + levelOfReview + ", oldLevelOfReview="
				+ oldLevelOfReview + ", agencySettingSubId=" + agencySettingSubId + ", levelId=" + levelId
				+ ", levelReviewerId=" + levelReviewerId + ", lastUpdateDate=" + lastUpdateDate + ", createdDate="
				+ createdDate + ", createdByUserId=" + createdByUserId + ", modifiedDate=" + modifiedDate
				+ ", modifiedByUserId=" + modifiedByUserId + ", allAgencyDetailsBeanList=" + allAgencyDetailsBeanList
				+ ", allReviewProcessBeanList=" + allReviewProcessBeanList + ", allAgencyUsersList="
				+ allAgencyUsersList + ", allLevel1UsersList=" + allLevel1UsersList + ", allLevel2UsersList="
				+ allLevel2UsersList + ", allLevel3UsersList=" + allLevel3UsersList + ", allLevel4UsersList="
				+ allLevel4UsersList + "]";
	}

	/**
	 * @param reviewLevelChangeInProgress the reviewLevelChangeInProgress to set
	 */
	public void setReviewLevelChangeInProgress(
			String reviewLevelChangeInProgress) {
		this.reviewLevelChangeInProgress = reviewLevelChangeInProgress;
	}

	/**
	 * @return the reviewLevelChangeInProgress
	 */
	public String getReviewLevelChangeInProgress() {
		return reviewLevelChangeInProgress;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

}