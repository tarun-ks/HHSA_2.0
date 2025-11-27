package com.nyc.hhs.model;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is a bean which maintains the Notification Settings information.
 *
 */

public class NotificationSettingsBean {

	@Length(max = 20)
	private String msSettingName;
	
	private String msSettingValue;

	public String getSettingName() {
		return msSettingName;
	}

	public void setSettingName(String msSettingName) {
		this.msSettingName = msSettingName;
	}

	public String getSettingValue() {
		return msSettingValue;
	}

	public void setSettingValue(String msSettingValue) {
		this.msSettingValue = msSettingValue;
	}

	@Override
	public String toString() {
		return "NotificationSettingsBean [msSettingName=" + msSettingName
				+ ", msSettingValue=" + msSettingValue + "]";
	}
	
	
	
}
