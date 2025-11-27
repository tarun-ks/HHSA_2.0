package com.nyc.hhs.report.action;

import javax.servlet.ServletContext;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.IPlatformContext;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformServletContext;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.model.api.DesignConfig;
import org.eclipse.birt.report.model.api.IDesignEngine;
import org.eclipse.birt.report.model.api.IDesignEngineFactory;
import org.eclipse.core.internal.registry.RegistryProviderFactory;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;

/**
 * This class is added for release 5. It is used to load the birt engine.
 */
public class BirtEngine
{

	private static IReportEngine moBirtEngine = null;
	private static Object moMutex = new Object();
	private static IDesignEngine moBirtDesignconfig = null;

	private static final LogInfo LOG_OBJECT = new LogInfo(BirtEngine.class);

	/**
	 * This method is used to get the birt engine report
	 * @param aoServletContext a servlet context object
	 * @return moBirtEngine
	 * @throws ApplicationException
	 */
	public static IReportEngine getBirtReportEngine(ServletContext aoServletContext) throws ApplicationException
	{
		try
		{
			if (moBirtEngine == null)
			{
				synchronized (moMutex)
				{
					if (moBirtEngine == null)
					{
						EngineConfig loEngineConfig = new EngineConfig();
						IPlatformContext loContext = new PlatformServletContext(aoServletContext);
						loEngineConfig.setPlatformContext(loContext);
						Platform.startup(loEngineConfig);
						IReportEngineFactory loFactory = (IReportEngineFactory) Platform
								.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
						moBirtEngine = loFactory.createReportEngine(loEngineConfig);
					}
				}
			}
		}
		catch (BirtException aoBirtExp)
		{
			LOG_OBJECT.Error("Exception occured while creating report in birt engine", aoBirtExp);
			throw new ApplicationException("Exception occured while creating report in birt engine", aoBirtExp);
		}

		return moBirtEngine;
	}

	/**
	 * This method is used to get birt design engine
	 * @param aoServletContext a servlet context object
	 * @return moBirtDesignconfig
	 * @throws ApplicationException
	 */
	public static IDesignEngine getBirtDesignEngine(ServletContext aoServletContext) throws ApplicationException
	{
		DesignConfig loDesignconfig = new DesignConfig();
		IPlatformContext loContext = new PlatformServletContext(aoServletContext);
		try
		{
			if (moBirtDesignconfig == null)
			{
				Platform.startup(loDesignconfig);
			}
			loDesignconfig.setPlatformContext(loContext);
			LOG_OBJECT.Info("Birt home from design config" + loDesignconfig.getBIRTHome());
			LOG_OBJECT.Info("Birt platform from design config" + loDesignconfig.getPlatformContext());
			Platform.startup(loDesignconfig);
			IDesignEngineFactory loDesignFactory = (IDesignEngineFactory) Platform
					.createFactoryObject(IDesignEngineFactory.EXTENSION_DESIGN_ENGINE_FACTORY);
			moBirtDesignconfig = loDesignFactory.createDesignEngine(loDesignconfig);
		}
		catch (BirtException aoBirtExp)
		{
			LOG_OBJECT.Error("Exception occured while creating factory object in birt engine", aoBirtExp);
			throw new ApplicationException("Exception occured while creating factory object in birt engine", aoBirtExp);
		}
		return moBirtDesignconfig;
	}

	/**
	 * This method is used to shutdown birt engine
	 */
	@SuppressWarnings("deprecation")
	public static synchronized void destroyBirtEngine()
	{
		if (moBirtEngine == null)
		{
			return;
		}
		moBirtEngine.shutdown();
		Platform.shutdown();
		RegistryProviderFactory.releaseDefault();
		moBirtEngine = null;
	}

	/**
	 * This method is used to destroy birt design engine
	 */
	public static synchronized void destroyBirtDesignEngine()
	{
		if (moBirtDesignconfig == null)
		{
			return;
		}
		Platform.shutdown();
		RegistryProviderFactory.releaseDefault();
		moBirtDesignconfig = null;
	}

}