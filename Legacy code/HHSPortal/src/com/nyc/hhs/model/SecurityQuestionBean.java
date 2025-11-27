package com.nyc.hhs.model;

import java.io.Serializable;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * This class is a bean which maintains the Security Question information.
 *
 */

public class SecurityQuestionBean implements Serializable  {
	
	private static final long serialVersionUID = -4022695846661447854L;
	@RegExp(value ="^\\d{0,3}")
	private int miquestionId;
	@Length(max = 200)
	private String msQuestionText;
	
	public final int getMiquestionId() {
		return miquestionId;
	}
	
	public final void setMiquestionId(int miquestionId) {
		this.miquestionId = miquestionId;
	}
	
	public final String getMsQuestionText() {
		return msQuestionText;
	}
	
	public final void setMsQuestionText(String msQuestionText) {
		this.msQuestionText = msQuestionText;
	}

	@Override
	public String toString() {
		return "SecurityQuestionBean [miquestionId=" + miquestionId
				+ ", msQuestionText=" + msQuestionText + "]";
	}

	
}
