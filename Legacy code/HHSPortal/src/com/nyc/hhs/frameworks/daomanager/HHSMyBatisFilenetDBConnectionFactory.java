package com.nyc.hhs.frameworks.daomanager;

import org.apache.ibatis.session.SqlSessionFactory;

import com.nyc.hhs.constants.ApplicationConstants;

/**
 * This class will return database connection object from application server
 * connection pool.
 * 
 */
public class HHSMyBatisFilenetDBConnectionFactory extends MyBatisConnectionFactory
{
	protected static SqlSessionFactory moSqlSessionFactory;

	/**
	 * This method returns SqlSessionFactory for BatchSqlMapConfigR2.xml. It calls
	 * getSessionFactory which implements actual code for building the factory.
	 * 
	 * @return SqlSessionFactory it is a factory object to build SqlSession
	 */

	public static SqlSessionFactory getSqlSessionFactory()
	{
		return getSqlSessionFactory(ApplicationConstants.FILENET_PE_SQL_CONFIG);
	}

	/**
	 * This method returns SqlSessionFactory for asConfigFile.
	 * 
	 * @param asConfigFile
	 *            Fully Qualified Name of Config XML file.
	 * @return SqlSessionFactory it is a factory object to build SqlSession
	 */

	protected static SqlSessionFactory getSqlSessionFactory(String asConfigFile)
	{
		moSqlSessionFactory =getSessionFactory(asConfigFile, moSqlSessionFactory);
		return moSqlSessionFactory;
	}
	
	/**
	 * This method returns SqlSessionFactory for BatchSqlMapConfigR2. It calls
	 * getLocalSessionFactory which implements actual code for building the
	 * factory.
	 * 
	 * @return SqlSessionFactory it is a factory object to build SqlSession
	 */

	public static SqlSessionFactory getLocalSqlSessionFactory()
	{
		return getLocalSqlSessionFactory(ApplicationConstants.LOCAL_FILENET_SQL_CONFIG);
	}

	/**
	 * This method returns SqlSessionFactory for asConfigFile.
	 * 
	 * @param asConfigFile
	 *            Fully Qualified Name of Config XML file.
	 * @return SqlSessionFactory it is a factory object to build SqlSession
	 */
	protected static SqlSessionFactory getLocalSqlSessionFactory(String asConfigFile)
	{
		moSqlSessionFactory = getLocalSessionFactory(asConfigFile, moSqlSessionFactory);
		return moSqlSessionFactory;
	}
}
