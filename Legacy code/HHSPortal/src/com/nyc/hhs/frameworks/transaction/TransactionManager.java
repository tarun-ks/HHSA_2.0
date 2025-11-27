package com.nyc.hhs.frameworks.transaction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
//log4j2 - do not remove! waiting for Java 7
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.daomanager.service.ServiceState;
import com.nyc.hhs.daomanager.service.TransactionLogService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * Transaction Manager is core class for executing services in a transaction. If
 * service fails complete is rolled back (automatic or manual through logs)
 * 
 */

public class TransactionManager
{
	private static final LogInfo LOG_OBJECT = new LogInfo(TransactionManager.class);

	/**
	 * This method is used to execute transaction for r1 with the name of the
	 * transaction ID passed to it
	 * 
	 * @param aoChannel channel object in which parameters are set and
	 *            communicated to transaction layer
	 * @param asTransactionId id of the transaction to be executed
	 * @return aoChannel channel object with the values manipulated by
	 *         transaction
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public static Channel executeTransaction(Channel aoChannel, String asTransactionId) throws ApplicationException
	{
		return executeTransaction(aoChannel, asTransactionId, ApplicationConstants.TRANSACTION_ELEMENT);
	}

	/**
	 * This method is used to execute one transaction with the name of the
	 * transaction ID passed to it
	 * 
	 * @param aoChannel channel object in which parameters are set and
	 *            communicated to transaction layer
	 * @param asTransactionId id of the transaction to be executed
	 * @param asTransactionCacheKey transaction type key
	 * @return aoChannel channel object with the values manipulated by
	 *         transaction
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	public static Channel executeTransaction(Channel aoChannel, String asTransactionId, String asTransactionCacheKey)
			throws ApplicationException
	{
		Transaction loTransaction = null;
		SqlSession loSession = null;
		SqlSession loFilenetPESession = null;
		P8UserSession loP8UserSession = null;
		Logger loTransLog = Logger.getLogger(TransactionManager.class);
		//Logger loTransLog = LogManager.getLogger(TransactionManager.class); //log4j2
		StringBuffer loTransactionLog = new StringBuffer();
		String loExceptionName = "";
		String loDetailedMessage = "";
		boolean lbIsTransactionFailed = false;
		String lsTransactionStartTime = CommonUtil.getCurrentTimeInMilliSec();
		String lsTransactionId = asTransactionId.concat("-").concat(String.valueOf(System.currentTimeMillis()));
		try
		{// Parse and read Transactions.xml
			Element loTransactionElement = getTransactionElement(asTransactionId, asTransactionCacheKey);
			loTransaction = getTransactionDetails(loTransactionElement);
			// Get service nodes for this transaction
			List loServiceEleList = loTransactionElement.getChildren();
			Iterator loServiceIter = loServiceEleList.iterator();
			boolean lbUsesDBConnection = false;
			if (loTransaction.isMbUseDBConnection())
			{
				loSession = MyBatisConnectionFactory.getSqlSessionFactory().openSession();
				aoChannel.setData(ApplicationConstants.MYBATIS_SESSION, loSession);
				lbUsesDBConnection = true;
			}
			else if (loTransaction.isMbUseLocalDBConnection())
			{
				loSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
				aoChannel.setData(ApplicationConstants.MYBATIS_SESSION, loSession);
				lbUsesDBConnection = true;
			}
			if (loTransaction.isMbUseFilenetConnection())
			{
				loFilenetPESession = HHSMyBatisFilenetDBConnectionFactory.getSqlSessionFactory().openSession();
				loP8UserSession = (P8UserSession) aoChannel.getData(ApplicationConstants.FILENET_SESSION);
				if (null != loP8UserSession)
				{
					loP8UserSession.setFilenetPEDBSession(loFilenetPESession);
					aoChannel.setData(ApplicationConstants.FILENET_SESSION, loP8UserSession);
				}
			}
			while (loServiceIter.hasNext())
			{
				Service loServiceBean = getServiceDetails((Element) loServiceIter.next(), aoChannel, lbUsesDBConnection);
				loTransactionLog.append(executeService(loServiceBean, aoChannel, lsTransactionId));
			}
			// If the transaction node usesconnection is true, then close the DB
			// connection
			String lsTransactionEndTime1 = CommonUtil.getCurrentTimeInMilliSec();
			float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsTransactionStartTime),
					CommonUtil.getItemDateInMIlisec(lsTransactionEndTime1));
			//Removing PT Tracing
			
			if (liTimediff > 3)
			{
				LOG_OBJECT.Error("!!!!!Ending transaction, TIME LAPSED," + liTimediff + ", Transaction Name,"	+ lsTransactionId); 
			}
			else
			{
				LOG_OBJECT.Error("Ending transaction :: TIME LAPSED :" + liTimediff, lsTransactionId); 
			}
			
			if (loTransaction.isMbUseDBConnection() || loTransaction.isMbUseLocalDBConnection())
			{
				loSession.commit();
			}
		}
		catch (ApplicationException aoAppEx)
		{
			loExceptionName = aoAppEx.getClass().toString();
			loDetailedMessage = aoAppEx.getMessage();
			loTransactionLog.append(loDetailedMessage);
			loTransactionLog
					.append("Error : Service State:")
					.append(aoAppEx.getContextData() != null ? ((Service) aoAppEx.getContextData().get("Service") != null ? ((Service) aoAppEx
							.getContextData().get("Service")).toString() : "")
							: "");
			loTransactionLog
					.append("Error : Channel State:")
					.append(aoAppEx.getContextData() != null ? ((Channel) aoAppEx.getContextData().get("Channel") != null ? ((Channel) aoAppEx
							.getContextData().get("Channel")).getData().toString() : "")
							: "");
			loTransactionLog.append("Error : Transaction State:").append(
					aoAppEx.getContextData() != null ? aoAppEx.getContextData().get("State") != null ? aoAppEx
							.getContextData().get("State").toString() : "" : "");
			lbIsTransactionFailed = true;
			try
			{
				if (loSession != null)
				{
					loSession.rollback();
				}
				loTransactionLog.append("Database roll back was successful");
			}
			catch (Exception aoExp)
			{
				loTransactionLog.append("Database roll back was a failure");
			}
			throw new ApplicationException("Error occurred while executing a transaction: ", aoAppEx);
		}
		catch (Throwable aoTh)
		{
			loExceptionName = aoTh.getCause().getClass().toString();
			loDetailedMessage = aoTh.getMessage();
			loTransactionLog.append(loDetailedMessage);
			lbIsTransactionFailed = true;
			try
			{
				if (loSession != null)
				{
					loSession.rollback();
				}
				loTransactionLog.append("Database roll back was successful");
			}
			catch (Exception aoExp)
			{
				loTransactionLog.append("Database roll back was a failure");
			}
			throw new ApplicationException("Error occurred while executing a transaction: ", aoTh);
		}
		finally
		{
			finallyProcessing(loTransaction, loSession, loFilenetPESession, loTransLog, loTransactionLog,
					loExceptionName, lbIsTransactionFailed);
			String lsTransactionEndTime1 = CommonUtil.getCurrentTimeInMilliSec();
			float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsTransactionStartTime),
					CommonUtil.getItemDateInMIlisec(lsTransactionEndTime1));
			/***** Removing PT loggings.******/
			if (liTimediff > 3)
			{
				LOG_OBJECT.Error("!!!!!finally transaction, TIME LAPSED," + liTimediff + ", Transaction Name,"
						+ lsTransactionId);
			}
			else
			{
				LOG_OBJECT.Error("finally transaction :: TIME LAPSED :" + liTimediff, lsTransactionId);
			}
		}
		return aoChannel;
	}

	/**
	 * This method handles the finally block of execute transaction method
	 * @param aoTransaction - transaction object
	 * @param aoSession - my batis session object
	 * @param aoFilenetPESession - filenet session object
	 * @param aoTransLog - logger object
	 * @param aoTransactionLog - transaction log to be written
	 * @param aoExceptionName - name of exception
	 * @param abIsTransactionFailed - flag depecting if transaction failed or
	 *            passed
	 */
	private static void finallyProcessing(Transaction aoTransaction, SqlSession aoSession,
			SqlSession aoFilenetPESession, Logger aoTransLog, StringBuffer aoTransactionLog, String aoExceptionName,
			boolean abIsTransactionFailed)
	{
		if (aoTransaction != null && aoSession != null)
		{ // check if connection is open then close it
			if (aoTransaction.isMbUseDBConnection())
			{
				aoSession.close();
			}
			else if (aoTransaction.isMbUseLocalDBConnection())
			{
				aoSession.close();
			}
			// Start QC 9585 R 8.9  remove password from logs
			String param = CommonUtil.maskPassword(aoTransactionLog);
			//aoTransLog.log(Level.INFO, aoTransactionLog.toString());
			aoTransLog.log(Level.INFO, param);
			// End QC 9585 R 8.9  remove password from logs
			
			if (abIsTransactionFailed)
			{
				// Start QC 9585 R 8.9  remove password from logs
				aoTransactionLog.delete(0, aoTransactionLog.length());
				aoTransactionLog.append(param);
				// End QC 9585 R 8.9  remove password from logs
				SqlSession loMybatisSession = null;
				try
				{
					if (aoTransaction.isMbUseDBConnection())
					{
						loMybatisSession = MyBatisConnectionFactory.getSqlSessionFactory().openSession();
					}
					else if (aoTransaction.isMbUseLocalDBConnection())
					{
						loMybatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
					}
					HashMap<String, Object> loTransationMap = new HashMap<String, Object>();
					loTransationMap.put("asTransactionDate", new Date(System.currentTimeMillis()));
					loTransationMap.put("asUserId", "User_ID");
					loTransationMap.put("asExceptionName", aoExceptionName);
					StringBuffer loTransactionDetails = new StringBuffer();
					loTransactionDetails.append(aoTransaction.getTransId()).append(" :").append(System.currentTimeMillis()).append(" :");
					loTransactionDetails.append(CommonUtil.getCallingFunctionInformation()).append(" :").append(aoTransactionLog);
					if (loTransactionDetails != null && loTransactionDetails.length() >= 4000)
					{
						loTransationMap.put("asTransactionDetails", loTransactionDetails.substring(0,4000));
					}
					else
					{
						loTransationMap.put("asTransactionDetails", loTransactionDetails.toString());
					}
					TransactionLogService loLogService = new TransactionLogService();
					loLogService.logTransactionFailure(loTransationMap, loMybatisSession);
				}
				catch (Exception aoEx)
				{
					aoTransLog.log(Level.ERROR, aoEx.getStackTrace());
				}
				finally
				{
					if (loMybatisSession != null)
					{
						loMybatisSession.commit();
						loMybatisSession.close();
					}
				}
			}
		}
		if (aoFilenetPESession != null)
		{
			aoFilenetPESession.close();
		}

	}

	/**
	 * This method is used to get the element corresponding to the transaction
	 * id from the transaction config xml file
	 * 
	 * @param asTransactionId id of the transaction for which element need to be
	 *            retrieved
	 * @param asTransactionCacheKey transaction type key
	 * @return loNodeElement Element node for the specified transaction id
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	protected static Element getTransactionElement(String asTransactionId, String asTransactionCacheKey)
			throws ApplicationException
	{
		// get transaction document from cache.
		Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(asTransactionCacheKey);

		if (loDoc == null)
		{
			throw new ApplicationException("Transaction XML hasnt been loaded into memory for transaction key: "
					+ ApplicationConstants.TRANSACTION_ELEMENT);
		}
		String lsXPath = "//" + ApplicationConstants.TRANSACTION_ELEMENT + "[(@id=\"" + asTransactionId + "\")]";
		Element loNodeElement = XMLUtil.getElement(lsXPath, loDoc);
		if (loNodeElement == null)
		{
			throw new ApplicationException("Transaction not configured: " + lsXPath + ". XML: \r\n"
					+ XMLUtil.getXMLAsString(loDoc));
		}
		return loNodeElement;
	}

	/**
	 * This method is used to retrieve the details for a transaction from the
	 * transaction element retrieve corresponding to each element
	 * 
	 * @param aoElement transaction element retrieved for the transaction id
	 * @return loTransaction transaction object containing all the details of a
	 *         transaction
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	protected static Transaction getTransactionDetails(Element aoElement) throws ApplicationException
	{
		Transaction loTransaction = new Transaction();
		loTransaction.setTransId(aoElement.getAttributeValue("id"));
		if (aoElement.getAttributeValue("usesdbconnection") != null
				&& aoElement.getAttributeValue("usesdbconnection").equalsIgnoreCase("true"))
		{
			loTransaction
					.setMbUseDBConnection("true".equalsIgnoreCase(aoElement.getAttributeValue("usesdbconnection")));
		}
		else if (aoElement.getAttributeValue("useslocaldbconnection") != null
				&& aoElement.getAttributeValue("useslocaldbconnection").equalsIgnoreCase("true"))
		{
			loTransaction.setMbUseLocalDBConnection(true);
		}
		if (aoElement.getAttributeValue("usesfilenetconnection") != null
				&& aoElement.getAttributeValue("usesfilenetconnection").equalsIgnoreCase("true"))
		{
			loTransaction.setMbUseFilenetConnection("true".equalsIgnoreCase(aoElement
					.getAttributeValue("usesfilenetconnection")));
		}
		return loTransaction;
	}

	/**
	 * This method is used to get the service details of a transaction for which
	 * element is retrieved
	 * 
	 * @param aoElement element object for the transaction
	 * @param aoChannel channel object containing parameters to execute the
	 *            transaction
	 * @param abUsesDBConnection boolean value to indicate whether to use
	 *            database connection or not
	 * @return loServiceBean service bean object containing all the details of
	 *         the service
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	protected static Service getServiceDetails(Element aoElement, Channel aoChannel, boolean abUsesDBConnection)
			throws ApplicationException
	{
		Service loServiceBean = new Service();
		HashMap loParamMap = new HashMap();
		ArrayList loMethodParamList = new ArrayList();
		loServiceBean.setClassname(aoElement.getAttributeValue("classname")); // Class
		// set
		loServiceBean.setMethodname(aoElement.getChild("method").getAttributeValue("name")); // Method
		// name
		// set
		// Output set
		loServiceBean.setMethodOutputName(aoElement.getChild("output").getAttributeValue("name"));
		loServiceBean.setMethodOutputType(aoElement.getChild("output").getAttributeValue("type"));
		// Look for input param in channel

		HashMap loData = aoChannel.getData();
		List loLParamList = XMLUtil.getElementList("param", aoElement.getChild("method")); // Change
		// this
		// and
		// search
		// by
		// Xpath
		Iterator loIterator = loLParamList.iterator();
		while (loIterator.hasNext())
		{ // Input Param set

			Element loParam = (Element) loIterator.next();

			if (aoElement.getAttributeValue("servicetype") != null
					&& aoElement.getAttributeValue("servicetype").equalsIgnoreCase("system"))
			{
				loServiceBean.setSystemServiceFlag(true);
				loParamMap.put((String) loParam.getAttributeValue("name"),
						loData.get(loParam.getAttributeValue("name")));
				// still needs to be decided where this hashmap needs to be
				// used.
			}
			else
			{
				Service.MethodParam loMethodParam = loServiceBean.new MethodParam();
				loMethodParam.setParamName(loParam.getAttributeValue("name"));
				loMethodParam.setParamType(loParam.getAttributeValue("type"));
				loMethodParamList.add(loMethodParam);
			}
		}
		if (loServiceBean.getSystemServiceFlag())
		{
			aoChannel.setData(aoElement.getAttributeValue("paramName"), loParamMap);
			if (abUsesDBConnection)
			{
				Service.MethodParam loMethodParamSession = loServiceBean.new MethodParam();
				loMethodParamSession.setParamName(ApplicationConstants.MYBATIS_SESSION);
				loMethodParamSession.setParamType("org.apache.ibatis.session.SqlSession");
				loMethodParamList.add(loMethodParamSession);
			}
			Service.MethodParam loMethodParam = loServiceBean.new MethodParam();
			loMethodParam.setParamName(aoElement.getAttributeValue("paramName"));
			loMethodParam.setParamType("java.util.HashMap");
			loMethodParamList.add(loMethodParam);

		}
		loServiceBean.setMethodParam(loMethodParamList);
		return loServiceBean;
	}

	/**
	 * This method is used to execute the service for which service details has
	 * been already fetched
	 * 
	 * @param aoServiceBean service bean with all the details of the service to
	 *            be executed
	 * @param aoChannel channel object containing all parameters required to
	 *            execute the transaction
	 * @return lsState state of the service execution
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	protected static String executeService(Service aoServiceBean, Channel aoChannel, String lsTransactionId)
			throws ApplicationException
	{
		String lsState = "";
		try
		{
			LOG_OBJECT.Error("!!!!!Start Service Name: " + aoServiceBean.getMethodname());
			// instantiate class
			Class loClassName = Class.forName(aoServiceBean.getClassname());
			// Create Class type arraylist to pass to reflection
			List loMethodParamList = aoServiceBean.getMethodParam();
			// array of paramater class objects
			Class[] loCInputParamType = new Class[loMethodParamList.size()];
			for (int liCounter = 0; liCounter < loMethodParamList.size(); liCounter++)
			{

				loCInputParamType[liCounter] = Class.forName(((Service.MethodParam) loMethodParamList.get(liCounter))
						.getParamType());
								
			}
			// Method object
			Method loClassMethod = loClassName.getMethod(aoServiceBean.getMethodname(), loCInputParamType);
			// Create Object type arraylist to pass to reflection
			Object[] loOInputParamObjects = new Object[loMethodParamList.size()];
			StringBuffer loParamaters = new StringBuffer(" Params:: ");
			String param = null;
			for (int liCounter = 0; liCounter < loMethodParamList.size(); liCounter++)
			{
				loOInputParamObjects[liCounter] = aoChannel.getData(((Service.MethodParam) loMethodParamList
						.get(liCounter)).getParamName());
				// Start QC 8998 R 8.8  remove password from logs
				param = CommonUtil.maskPassword(loOInputParamObjects[liCounter]);
							
				loParamaters.append(((Service.MethodParam) loMethodParamList.get(liCounter)).getParamName() + " => "
						//+ loOInputParamObjects[liCounter] + " ");
						+ param + " ");
				// End QC 8998 R 8.8  remove password from logs
			}

			// Instantiate
			Object loObj = loClassName.newInstance();
			// invoke the function and set the output in channel
			((ServiceState) loObj).setChannel(aoChannel);
			try
			{
				// invoke the function and set the output in channel
				String lsTransactionStartTime = CommonUtil.getCurrentTimeInMilliSec();
				LOG_OBJECT.Debug("Start Method:: " + aoServiceBean.getMethodname() + loParamaters);
				aoChannel.setData(
						aoServiceBean.getMethodOutputName(),
						Class.forName(aoServiceBean.getMethodOutputType()).cast(
								loClassMethod.invoke(loObj, loOInputParamObjects)));
				String lsTransactionEndTime = CommonUtil.getCurrentTimeInMilliSec();
				float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsTransactionStartTime),
						CommonUtil.getItemDateInMIlisec(lsTransactionEndTime));
				/***** Removing PT loggings.******/
				if (liTimediff > 3)
				{  //jm
					//LOG_OBJECT.Error("!!!!!Ending Service, TIME LAPSED," + liTimediff + ", Transaction Name,"
							//+ lsTransactionId + ", Service Name," + aoServiceBean.getMethodname());
				}
				else
				{
					LOG_OBJECT.Info("Ending executeService :: TIME LAPSED : " + liTimediff +" "+ lsTransactionId);
				}
			}
			finally
			{
				lsState = getServiceLog(loClassName, loObj);
			}
			return lsState;
		}
		catch (Throwable loTh)
		{
			if (!(loTh.getCause() instanceof ApplicationException))
			{
				LOG_OBJECT.Error("Error occured while executing service", loTh);
				ApplicationException loEx = new ApplicationException("Error occured while executing service: "
						+ aoServiceBean.getClassname(), loTh);
				loEx.addContextData("Channel", aoChannel);
				loEx.addContextData("Service", aoServiceBean);
				loEx.addContextData("State", lsState);
				throw loEx;
			}
			else
			{
				ApplicationException loEx = (ApplicationException) loTh.getCause();
				loEx.addContextData("Channel", aoChannel);
				loEx.addContextData("Service", aoServiceBean);
				loEx.addContextData("State", lsState);
				throw loEx;
			}
		}
	}

	/**
	 * This method is used to ge the logger object for the service class
	 * 
	 * @param aoClassName service class name
	 * @param aoServiceObject service object containing all the details of the
	 *            service
	 * @return lsServiceLog service log
	 * @throws ApplicationException when any exception occurred we wrap it into
	 *             this custom exception
	 */
	protected static String getServiceLog(Class aoClassName, Object aoServiceObject) throws ApplicationException
	{
		StringBuffer lsServiceLog = null;
		Class loSuperClass = aoClassName.getSuperclass();
		String lsMethodName = "getMoState";

		try
		{
			Method loClassMethod = loSuperClass.getDeclaredMethod(lsMethodName);
			// loClassMethod.getExceptionTypes();
			lsServiceLog = (StringBuffer) loClassMethod.invoke(loSuperClass.cast(aoServiceObject));
		}
		catch (Exception aoExp)
		{
			lsServiceLog = new StringBuffer("Failed to fetch method :: " + lsMethodName + " ::  of " + loSuperClass + " :: class");
		}
		return lsServiceLog == null ? "" : lsServiceLog.toString();
	}
	
	// TODO to be deleted
	public static Channel executeTransaction(Channel aoChannel, String asTransactionId, String asTransactionCacheKey,
			boolean abFlag) throws ApplicationException
	{
		return aoChannel;
	}
}
