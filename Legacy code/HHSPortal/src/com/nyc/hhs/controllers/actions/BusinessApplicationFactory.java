package com.nyc.hhs.controllers.actions;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This class reads the property file(businessapplicationfactory.properties) and fetch
 * the reference of the class based on the section and subsection name.
 * 
 */

public class BusinessApplicationFactory {
	
	/**
	 * This method calls the factory based on section and subsection name
	 * 
	 * @param asSectionName - current section name
	 * @param asSubSectionName - current sub section name
	 * @return the object of businessApplication for corresponding section and subsection name
	 * @throws ApplicationException
	 */
	public static BusinessApplication getBusinessApplication(String asSectionName, String asSubSectionName) throws ApplicationException{
		BusinessApplication loBusinessApplication = null;
		//block of code to be executed if asSubSectionName and asSectionName both are not null
		 if( null != asSubSectionName && null != asSectionName){
			 try {
				String lsClass = PropertyLoader.getProperty(ApplicationConstants.BA_FACTORY_PROP_FILE, asSectionName + "_s3p4r4t0r_" + asSubSectionName);
				Class loClass = Class.forName(lsClass);
				loBusinessApplication = (BusinessApplication)loClass.newInstance(); 
			} catch (ApplicationException loAe) {
				throw new ApplicationException("Error occured while fetching factory for BusinessApplication", loAe);
			} catch (ClassNotFoundException loCNFE) {
				throw new ApplicationException("Error occured while fetching factory for BusinessApplication", loCNFE);
			} catch (InstantiationException loIE) {
				throw new ApplicationException("Error occured while fetching factory for BusinessApplication", loIE);
			} catch (IllegalAccessException loIAE) {
				throw new ApplicationException("Error occured while fetching factory for BusinessApplication", loIAE);
			}
		}
		return loBusinessApplication;
	}
}
