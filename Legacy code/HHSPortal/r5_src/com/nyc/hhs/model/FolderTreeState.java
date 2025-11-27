package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
//This is a Bean class added for Folder tree functionality
public class FolderTreeState
{
	private boolean hidden, selected, disabled, opened;
	@Length(max = 40)
	//FOLDER_FILENET_ID
	private String folderId;
	
	
	public FolderTreeState()
	{
		super();
	}

	public String getFolderId()
	{
		return folderId;
	}

	public void setFolderId(String folderId)
	{
		this.folderId = folderId;
	}

	public boolean isHidden()
	{
		return hidden;
	}

	public void setHidden(boolean hidden)
	{
		this.hidden = hidden;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	public boolean isDisabled()
	{
		return disabled;
	}

	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}

	public boolean isOpened()
	{
		return opened;
	}

	public void setOpened(boolean opened)
	{
		this.opened = opened;
	}
}
