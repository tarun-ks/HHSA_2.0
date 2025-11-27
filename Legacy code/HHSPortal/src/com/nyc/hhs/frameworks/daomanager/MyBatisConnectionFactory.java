package com.nyc.hhs.frameworks.daomanager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This class will return database connection object from application server
 * connection pool.
 * 
 */
public class MyBatisConnectionFactory
{
	private static final LogInfo LOG_OBJECT = new LogInfo(MyBatisConnectionFactory.class);

	protected static SqlSessionFactory moSqlSessionFactory;
	private static final String ENVIRONMENT = System.getProperty("hhs.env");

	/**
	 * This method returns SqlSessionFactory for BatchSqlMapConfig.xml. It calls
	 * getSessionFactory which implements actual code for building the factory.
	 * 
	 * @return SqlSessionFactory it is a factory object to build SqlSession
	 */

	public static SqlSessionFactory getSqlSessionFactory()
	{
		return getSqlSessionFactory(ApplicationConstants.SQL_CONFIG);
	}

	/**
	 * This method returns SqlSessionFactory for asConfigFile.
	 * 
	 * @param asConfigFile Fully Qualified Name of Config XML file.
	 * @return SqlSessionFactory it is a factory object to build SqlSession
	 */

	protected static SqlSessionFactory getSqlSessionFactory(String asConfigFile)
	{
		moSqlSessionFactory = getSessionFactory(asConfigFile, moSqlSessionFactory);
		return moSqlSessionFactory;
	}

	/**
	 * This method returns SqlSessionFactory for asConfigFile. It implements
	 * actual code for building the factory.
	 * 
	 * @param asConfigFile Fully Qualified Name of Config XML file.
	 * @param aoSqlSessionFactory it is a factory object to build SqlSession
	 * @return SqlSessionFactory it is a factory object to build SqlSession
	 */

	protected static SqlSessionFactory getSessionFactory(String asConfigFile, SqlSessionFactory aoSqlSessionFactory)
	{
		try
		{
			if (aoSqlSessionFactory == null)
			{
				synchronized (asConfigFile)
				{
					if (aoSqlSessionFactory == null)
					{
						Reader loReader = Resources.getResourceAsReader(asConfigFile);
						aoSqlSessionFactory = new SqlSessionFactoryBuilder().build(loReader);
					}
				}
			}
		}
		catch (FileNotFoundException aoExp)
		{
			LOG_OBJECT.Error("FileNotFoundException occurred while reading sql config file:" + aoExp.getMessage());
		}
		catch (IOException aoExp)
		{
			LOG_OBJECT.Error("IOException occurred while reading sql config file:" + aoExp.getMessage());
		}
		return aoSqlSessionFactory;
	}

	/**
	 * This method returns SqlSessionFactory for BatchSqlMapConfig.xml. It calls
	 * getLocalSessionFactory which implements actual code for building the
	 * factory.
	 * 
	 * @return SqlSessionFactory it is a factory object to build SqlSession
	 */

	public static SqlSessionFactory getLocalSqlSessionFactory()
	{
		return getLocalSqlSessionFactory(ApplicationConstants.LOCAL_SQL_CONFIG);
	}

	/**
	 * This method returns SqlSessionFactory for asConfigFile.
	 * 
	 * @param asConfigFile Fully Qualified Name of Config XML file.
	 * @return SqlSessionFactory it is a factory object to build SqlSession
	 */
	protected static SqlSessionFactory getLocalSqlSessionFactory(String asConfigFile)
	{
		moSqlSessionFactory = getLocalSessionFactory(asConfigFile, moSqlSessionFactory);
		return moSqlSessionFactory;
	}

	/**
	 * This method returns SqlSessionFactory for asConfigFile. It implements
	 * actual code for building the factory.
	 * 
	 * @param asConfigFile Fully Qualified Name of Config XML file.
	 * @return SqlSessionFactory it is a factory object to build SqlSession
	 */

	protected static SqlSessionFactory getLocalSessionFactory(String asConfigFile, SqlSessionFactory aoSqlSessionFactory)
	{
		try
		{
			if (aoSqlSessionFactory == null)
			{
				synchronized (asConfigFile)
				{
					if (aoSqlSessionFactory == null)
					{
						String lsEnvironment = ENVIRONMENT;
						if (lsEnvironment != null)
						{
							if (lsEnvironment.indexOf("_") > 0)
							{
								lsEnvironment = lsEnvironment.split("_")[0].toLowerCase();
							}
						}
						Reader loReader = Resources.getResourceAsReader(asConfigFile);
						aoSqlSessionFactory = new SqlSessionFactoryBuilder().build(loReader, lsEnvironment);
					}
				}
			}
		}
		catch (FileNotFoundException aoExp)
		{
			LOG_OBJECT
					.Error("FileNotFoundException occurred while reading batch sql config file:" + aoExp.getMessage());
		}
		catch (IOException aoExp)
		{
			LOG_OBJECT.Error("IOException occurred while reading batch sql config file:" + aoExp.getMessage());
		}
		return aoSqlSessionFactory;
	}
}
