package com.nyc.hhs.frameworks.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InterruptedIOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

public class RollingZipFileAppender extends RollingFileAppender {
	
	private String zipFolderPath;

	@Override
	protected void subAppend(LoggingEvent event) 
	{
		cleanupAndRollOver();
		super.subAppend(event);
	}

	public String getZipFolderPath() {
		return zipFolderPath;
	}

	public void setZipFolderPath(String zipFolderPath) {
		this.zipFolderPath = zipFolderPath;
	}

	protected void cleanupAndRollOver() 
	{
		String lsFolderPath = "";
		try
		{
			lsFolderPath = getFile().substring(0, getFile().lastIndexOf("/") + 1);
			String lsFileName = getFile().substring(getFile().lastIndexOf("/") + 1);
			String lsOutputZipFilePath = "";
			String lsOutputZipFolerPath = "";
			if (null != getZipFolderPath() && !getZipFolderPath().isEmpty())
			{
				lsOutputZipFolerPath = getZipFolderPath();
			}
			else
			{
				lsOutputZipFolerPath = lsFolderPath;
			}
			lsOutputZipFilePath = lsOutputZipFolerPath + lsFileName +System.currentTimeMillis()+ ".zip";
			
			
			 if (this.maxBackupIndex > 0)
			    {
			      File file = new File(this.fileName + '.' + this.maxBackupIndex);
			      if (file.exists()) 
			      {
			       byte[] buffer = new byte[8102];
			       FileOutputStream fos = new FileOutputStream(lsOutputZipFilePath);
			       ZipOutputStream zos = new ZipOutputStream(fos);
			       LogLog.debug("Log Zip Creation Started!! Start time: " +System.currentTimeMillis());
			       for (int i = 1; i <= this.maxBackupIndex; i++)
			       {
			    	   ZipEntry ze = new ZipEntry(lsFileName+'.'+i);
			    	   zos.putNextEntry(ze);
			    	   FileInputStream in = new FileInputStream(lsFolderPath + File.separator + lsFileName+'.'+i);
			    	   int len;
			    	   while ((len = in.read(buffer)) > 0) 
			    	   {
			    		   zos.write(buffer, 0, len);
			    	   }
			    	   LogLog.debug("Added file " + lsFileName+'.'+i + " to Zip");
			    	   in.close();
			    	   File fileToDelete = new File(this.fileName + '.' +i);
			    	   fileToDelete.delete();
			    	   LogLog.debug("Deleted file " + lsFileName+'.'+i + " from Log Directory");
			       }
			       zos.closeEntry();
			       zos.close();
			       LogLog.debug("Log Zip Creation Completed!! End Time: " +System.currentTimeMillis());
			      }
			}
		}
		catch(Exception e)
		{
			if ((e instanceof InterruptedIOException)) {
	              Thread.currentThread().interrupt();
	            }
	            LogLog.error("RollingZipFileAppender Archiving failed due to: "+ e.getMessage());
		}
		

	}
}
