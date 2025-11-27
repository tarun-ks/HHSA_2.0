package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class NYCAgencyID {

	 @Length(max = 20)
	 String msNYCAgencyID ="";

	 
	 public String getMsNYCAgencyID() {
		return msNYCAgencyID;
	}

	public void setMsNYCAgencyID(String msNYCAgencyID) {
		this.msNYCAgencyID = msNYCAgencyID;
	}
	
	@Override
	public String toString() {
		return msNYCAgencyID;
	}
}
