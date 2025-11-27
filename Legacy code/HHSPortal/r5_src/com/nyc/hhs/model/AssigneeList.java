package com.nyc.hhs.model;
//This is a Bean class added in Release 5 for assignee list functionality
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class AssigneeList
{
	private String taskLevel;
	private String taskType;
	private String isDefault;
	private String askFlag;
	@Length(max = 20)
	private String reviewerId;
	private List<UserDetailBean> userDetailBean;
	private String userId;
	private String msTotalLevel;
	private String msModifiedDate;
	private String addedBy;

	
	public AssigneeList()
	{
		super();
	}

	public String getAddedBy()
	{
		return addedBy;
	}

	public void setAddedBy(String addedBy)
	{
		this.addedBy = addedBy;
	}

	public String getMsModifiedDate()
	{
		return msModifiedDate;
	}

	public void setMsModifiedDate(String msModifiedDate)
	{
		this.msModifiedDate = msModifiedDate;
	}

	public String getMsTotalLevel()
	{
		return msTotalLevel;
	}

	public void setMsTotalLevel(String msTotalLevel)
	{
		this.msTotalLevel = msTotalLevel;
	}
	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public List<UserDetailBean> getUserDetailBean()
	{
		return userDetailBean;
	}

	public void setUserDetailBean(List<UserDetailBean> userDetailBean)
	{
		this.userDetailBean = userDetailBean;
	}

	public String getTaskLevel()
	{
		return taskLevel;
	}

	public void setTaskLevel(String taskLevel)
	{
		this.taskLevel = taskLevel;
	}

	public String getTaskType()
	{
		return taskType;
	}

	public void setTaskType(String taskType)
	{
		this.taskType = taskType;
	}

	public String getIsDefault()
	{
		return isDefault;
	}

	public void setIsDefault(String isDefault)
	{
		this.isDefault = isDefault;
	}

	public String getAskFlag()
	{
		return askFlag;
	}

	public void setAskFlag(String askFlag)
	{
		this.askFlag = askFlag;
	}

	public String getReviewerId()
	{
		return reviewerId;
	}

	public void setReviewerId(String reviewerId)
	{
		this.reviewerId = reviewerId;
	}

}
