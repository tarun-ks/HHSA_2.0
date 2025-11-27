package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the NYC Agency information.
 *
 */

public class NYCAgency {
 
 @Length(max = 100)
 String msNYCAgencyName = "";

 public String getMsNYCAgencyName() {
  return msNYCAgencyName;
 }

 public void setMsNYCAgencyName(String msNYCAgencyName) {
  this.msNYCAgencyName = msNYCAgencyName;
 }

@Override
public String toString() {
	return "NYCAgency [msNYCAgencyName=" + msNYCAgencyName + "]";
}
 
}

