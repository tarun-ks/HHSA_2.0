/**
 * 
 */
package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.BulkDownloadBean;
import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.FolderMappingBean;
import com.nyc.hhs.model.OrganizationBean;

/**
 * This Mapper helps to set folder mapping properties whenever a folder is
 * updated moved deleted or changed
 * 
 * This is a mapper class which has queries to perform functions and get data
 * for Folder Mapping class
 */
public interface FolderMappingMapper
{
	public int deleteFolder(HashMap<String, Object> aoMap);

	public int emptyRecycleBin(String aoOrgId);

	public FolderMappingBean getDuplicateId(String aoOrgId);

	// Added for Release 5

	public int deleteFolderChildList(HashMap<String, String> aoHashMap);

	public List<OrganizationBean> fetchOrganizationDetails();

	public List<BulkDownloadBean> getDownloadList(String aoStatusId);

	public int updateStatusFlag(String aoReqId);
	
	public int updateStatusFlagDownload(String aoReqId);

	public int updateExportStatusFlag(String aoReqId);
	
	public String getOrgId(String aoFldrId);
	
	public String getOrgLegalName(String lsProviderId);

	public List<String> getZipId(String aoReqId);

	public int insertForDownloadAll(HashMap<String, String> aoHashmap);

	public int setentryintoZipTable(HashMap<String, String> aoHashmap);

	public int createFolder(FolderMappingBean aoFolderMappingBean);

	public String getPath(HashMap<String, String> aoHashmap);

	public HashMap<String, Object> getZipPath(HashMap<String, String> aoHashmap);

	public HashMap<String, Object> getExportInfoFromDB(HashMap<String, String> aoHashmap);
	
	public FolderMappingBean fetchJsTree(HashMap<String, String> aoHashmap);

	public List<FolderMappingBean> getLockInfo(HashMap<String, String> aoHashmap);

	public int updateFolderMapping(FolderMappingBean aoFolderMappingBean);

	public FolderMappingBean checkFolderNameExists(HashMap<String, String> aoHashmap);

	public List<FolderMappingBean> getCount(HashMap<String, String> aoHashmap);

	public Integer insertForFilenet(FolderMappingBean aoFolderBean);

	public Integer insertForFilenetInBatch(FolderMappingBean aoFolderBean);

	public Integer updateFolderCount(FolderMappingBean aoFolderBean);

	public List<FolderMappingBean> fetchFolderMapList(HashMap<String, String> aoHashmap);

	public List<FolderMappingBean> fetchFolderMapListOrg(HashMap<String, String> aoHashmap);

	public FolderMappingBean getFolderProp(HashMap aoHashMap);

	public Integer decreaseCount(FolderMappingBean aoFolderBean);

	public Integer increaseCount(FolderMappingBean aoFolderBean);

	public List<DocumentBean> fetchData(HashMap aoHashMap);

	public Integer updateDocCount(FolderMappingBean aoFolderBean);

	public Integer updateDocCountDynamic(HashMap<String, String> aoHashMap);

	public int deleteFolderSingleRow(HashMap<String, String> aoHashMap);

	public int getParentId(HashMap<String, Object> aoHashMap);

	public int setTreeFlag(HashMap<String, Object> aoHashMap);

	public int setMoveFlag(HashMap<String, Object> aoHashMap);

	public int setParentId(HashMap<String, Object> aoHashMap);

	public List<String> getProcurementInfo(HashMap<String, Object> aoHashMap);

	public List<ExtendedDocument> getProcurementTitle(HashMap<String, Object> aoHashMap);

	public List<String> getAwardEPin(HashMap<String, Object> aoHashMap);

	public List<String> getContractAwardEPin(HashMap<String, Object> aoHashMap);

	public FolderMappingBean getFolderCount(HashMap aoHashMap);

	public List<String> getAmendmentEpin(Map<String, String> aoDataMap);

	public List<String> fetchChar500LinkageInfo(HashMap aoHashMap);
	
	public Integer fetchFolderPathLength(HashMap<String, String> aoHashmap);
	
	public String checkFolderExistsInDb(String aoFolderId);
	
	public List<String> docIdForLinkage();
	
	public Integer insertFilenetAudit(FolderMappingBean aoFolderBean);
	
	public Integer insertFilenetAuditForBatch(HashMap<String,String> aoMap);
	
	
	
	
}
