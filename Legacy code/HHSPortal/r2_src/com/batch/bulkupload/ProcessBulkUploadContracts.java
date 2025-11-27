package com.batch.bulkupload;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;

/**
 * This interface will be implemented by classes want to process bulk upload
 * data files and template if you are create a new class for supporting a new
 * template, please keep below point is mind 1. Please refer
 * TemplateProcess1Impl.java class 2. Make new class entry in
 * templatefactory.property for that template version.
 */
public interface ProcessBulkUploadContracts
{

	 boolean checkMandatoryFields(BulkUploadContractInfo aoContractDetails) throws ApplicationException;

	 boolean processOptionalFields(BulkUploadContractInfo aoContractDetails) throws ApplicationException;

	 boolean saveContract(BulkUploadContractInfo aoContractDetails) throws ApplicationException;

	 boolean validateBulkContractSpreadsheet() throws ApplicationException;

	 void setTemplateFileObj(Object aoTemplateFileObj);

	 void setDataFileObj(Object aoDataFileObj);

	 String processData() throws ApplicationException;

	 void setAdditionalFieldProcessObj(AdditionalFiledProcessing aoBusinessRule);

	void setMoUserSession(P8UserSession moUserSession);

	void setFileUploadedByUser(String fileUploadedByUser);

	void setFileUploadId(String fileUploadId);
}
