package com.nyc.hhs.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.jdom.Document;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AddressBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * This servlet will validate address using web services whenever a form is
 * submitted containing address fields.
 */

public class AddressValidationServlet extends HttpServlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(AddressValidationServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 * This is the non-parameterized constructor
	 */
	public AddressValidationServlet()
	{
		super();
	}

	/**
	 * This method handle the get request of a servlet. This will internally
	 * call doPost method to process the servlet request and return the
	 * response.
	 * 
	 * @param aoRequest HttpServlet request object
	 * @param aoResponse HttpServlet response object
	 * @throws ServletException If an Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	protected void doGet(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		this.doPost(aoRequest, aoResponse);
	}

	/**
	 * This method handle the post request of a servlet. When a action is
	 * initiated from a jsp, it process the action by calling multiple
	 * transactions to the end.
	 * Made changes in this method for defect id 6435 release 3.2.0.
	 * @param aoRequest HttpServlet request object
	 * @param aoResponse HttpServlet response object
	 * @throws ServletException If an Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	protected void doPost(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		UserThreadLocal.setUser(lsUserId);
		String lsStreetName = aoRequest.getParameter("address1");
		String lsCity = aoRequest.getParameter("city");
		String lsState = aoRequest.getParameter("state");
		String lsZipCode = aoRequest.getParameter("zipcode");
		String lsStartTime = "";
		lsStartTime = CommonUtil.getCurrentTimeInMilliSec();
		boolean lbBypassValidation = false;
		List<AddressBean> loAddressList = new ArrayList<AddressBean>();
		AddressBean loAddressBean = new AddressBean();
		loAddressBean.setMsAddress1(lsStreetName);
		loAddressBean.setMsCity(lsCity);
		loAddressBean.setMsState(lsState);
		loAddressBean.setMsZipcode(lsZipCode);
		loAddressBean.setMsAddressType("Original Address");
		loAddressList.add(loAddressBean);
		AddressBean loSuggestedAddressBean = new AddressBean();
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData("asZipCode", lsZipCode);
			TransactionManager.executeTransaction(loChannel, "searchZipCodeTran");
			String lsAddressSearchType = "NONNYC";
			String lsBorough = "";
			Map loMap = (Map) loChannel.getData("loMap");
			if (null != loMap && !loMap.isEmpty())
			{
				lsBorough = (String) loMap.get("BOROUGH");
				lsAddressSearchType = "NYC";
			}
			String lsAddressUrl = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					ApplicationConstants.ADDRESS_VALIDATION_URL);
			URL loOracle = new URL(lsAddressUrl + "?StreetName=" + lsStreetName + "&PostalCode=" + lsZipCode
					+ "&AddressSearchType=" + lsAddressSearchType + "&Borough=" + lsBorough);

			loChannel = new Channel();
			loChannel.setData("asURL", loOracle);
			TransactionManager.executeTransaction(loChannel, "addressValidationServiceCall");
			BufferedReader loBReader = (BufferedReader) loChannel.getData("loBufferedReader");

			String loInputLine;
			StringBuilder loSB = new StringBuilder();
			while ((loInputLine = loBReader.readLine()) != null)
			{
				loSB.append(loInputLine);
			}
			loBReader.close();
			loInputLine = loSB.toString();
			StringReader loSR = new StringReader(loInputLine);
			Document loResult = XMLUtil.getDomObj(loSR);
			if (null != loResult)
			{
				LOG_OBJECT.Debug("loResultv :" + XMLUtil.getXMLAsString(loResult));
			}
			if (null != loResult && null != loResult.getRootElement()
					&& loResult.getRootElement().getChildren().size() > 1)
			{
				lbBypassValidation = processServletResult(aoRequest, lsStreetName, lsCity, lsState, lsZipCode,
						lbBypassValidation, loAddressList, loAddressBean, loSuggestedAddressBean, lsBorough, loResult);
			}
			else
			{
				aoRequest.setAttribute("addressList", loAddressList);
				aoRequest
						.setAttribute(
								"description",
								"The address could not be verified.  Click Select to choose the address or Cancel to return to the previous screen and edit the address.");
				LOG_OBJECT.Debug("Address Result map is null or do not have any children");
			}
		}
		catch (ApplicationException aoExp)
		{
			aoRequest.setAttribute("addressList", loAddressList);
			aoRequest.setAttribute("addressUnavailable", true);
			aoRequest.setAttribute("description",
					"The address validation service is currently unavailable. Click OK to use the entered address.");
			LOG_OBJECT.Error("Error occured while getting address suggestion.", aoExp);
		}
		catch (HttpException aoExp)
		{
			aoRequest.setAttribute("addressList", loAddressList);
			aoRequest.setAttribute("addressUnavailable", true);
			aoRequest.setAttribute("description",
					"The address validation service is currently unavailable. Click OK to use the entered address.");
			LOG_OBJECT.Error("Error occured while getting address suggestion.", aoExp);
		}
		catch (IOException aoExp)
		{
			aoRequest.setAttribute("addressList", loAddressList);
			aoRequest.setAttribute("addressUnavailable", true);
			aoRequest.setAttribute("description",
					"The address validation service is currently unavailable. Click OK to use the entered address.");
			LOG_OBJECT.Error("Error occured while getting address suggestion.", aoExp);
		}
		//fix as a part of defect 6435 release 3.2.0
		catch (Exception aoExp)
		{
			aoRequest.setAttribute("addressList", loAddressList);
			aoRequest.setAttribute("addressUnavailable", true);
			aoRequest.setAttribute("description",
					"The address validation service is currently unavailable. Click OK to use the entered address.");
			LOG_OBJECT.Error("Error occured while getting address suggestion.", aoExp);
		}
		aoResponse.setContentType("text/html");
		if (lbBypassValidation)
		{
			final PrintWriter loOut = aoResponse.getWriter();
			final StringBuffer loOutputBuffer = new StringBuffer();
			loOutputBuffer.append("byPassValidation");
			loOut.print(loOutputBuffer.toString());
			loOut.flush();
		}
		String lsEndTime = CommonUtil.getCurrentTimeInMilliSec();
		float liTimediff = CommonUtil.timeDiff(CommonUtil.getItemDateInMIlisec(lsStartTime),
				CommonUtil.getItemDateInMIlisec(lsEndTime));
		if (liTimediff > 3)
		{
			LOG_OBJECT.Error("!!!!!Ending AddressValidationServlet, TIME LAPSED," + liTimediff);
		}
		try
		{
			LOG_OBJECT
					.Debug("AddressValidationServlet: validate address for NYC and NONNYC. method:doPost. Time Taken(seconds):: "
							+ liTimediff);
		}
		catch (ApplicationException aoException)
		{
			LOG_OBJECT.Error("ApplicationException occurred in doPost of AddressValidationServlet ", aoException);
		}
		UserThreadLocal.unSet();
		RequestDispatcher loDisp = getServletContext().getRequestDispatcher("/addressvalidation.jsp");
		loDisp.include(aoRequest, aoResponse);
	}

	/**
	 * This method will hit the servlet URL and get the result object and
	 * process it.
	 * Made changes in this method for defect id 6435 release 3.2.0.
	 * @param aoRequest an HttpServletRequest object
	 * @param asStreetName a string value of street name
	 * @param asCity a string value of city
	 * @param asState a string value of state
	 * @param asZipCode a string value of zip code
	 * @param abBypassValidation a boolean value indicating address match
	 * @param aoAddressList a list of address bean
	 * @param loAddressBean a original address bean object
	 * @param loSuggestedAddressBean a suggested address bean object
	 * @param lsBorough a string value of borough
	 * @param loResult a result object
	 * @return result object
	 * @throws ApplicationException
	 */
	private boolean processServletResult(HttpServletRequest aoRequest, String asStreetName, String asCity,
			String asState, String asZipCode, boolean abBypassValidation, List<AddressBean> aoAddressList,
			AddressBean loAddressBean, AddressBean loSuggestedAddressBean, String lsBorough, Document loResult)
			throws ApplicationException
	{
		String lsStatusCode = loResult.getRootElement().getChildText("StatusText");
		//String lsStatusDescriptionText = loResult.getRootElement().getChildText("StatusDescriptionText");
		if (lsStatusCode != null)
		{
			if (Integer.parseInt(lsStatusCode) == 13)
			{
				//throw new ApplicationException(lsStatusDescriptionText);
				//fix as a part of defect 6435 release 3.2.0
				aoRequest.setAttribute("addressList", aoAddressList);
				aoRequest
						.setAttribute(
								"description",
								"The zip code entered is invalid.  Click Select to choose the address or Cancel to return to the previous screen and edit the address.");
				LOG_OBJECT.Debug("Address Result map is null or do not have any children");
			
			}
			else
			{
				String lsSuggestedStreetName = isNull(loResult.getRootElement().getChildText("StreetName"));
				String lsSuggestedCityName = isNull(loResult.getRootElement().getChildText("LocationCityName"));
				String lsSuggestedState = isNull(loResult.getRootElement().getChildText(
						"LocationStateUSPostalServiceCode"));
				String lsSuggestedZipCode = isNull(loResult.getRootElement().getChildText("LocationPostalcode"));
				String lsStatusDescription = isNull(loResult.getRootElement().getChildText("StatusDescriptionText"));
				String lsStatusReason = isNull(loResult.getRootElement().getChildText("StatusReason"));
				String lsStreetNumberText = isNull(loResult.getRootElement().getChildText("StreetNumberText"));
				String lsCongressionalDistrictName = isNull(loResult.getRootElement().getChildText(
						"CongressionalDistrictName"));
				String lsLatitude = isNull(loResult.getRootElement().getChildText("YCoordinate"));
				String lsLongitude = isNull(loResult.getRootElement().getChildText("XCoordinate"));
				if (null != lsStreetNumberText && !lsStreetNumberText.equals(""))
				{
					lsSuggestedStreetName = new StringBuffer(lsStreetNumberText).append(" ")
							.append(lsSuggestedStreetName).toString();
				}
				String lsXCoordinate = "";
				String lsYCoordinate = "";
				String lsCommunityDistrict = "";
				String lsCivilCourtDistrict = "";
				String lsSchoolDistrictName = "";
				String lsHealthArea = "";
				String lsBuildingIdNumber = "";
				String lsTaxBlock = "";
				String lsTaxLot = "";
				String lsSenatorialDistrict = "";
				String lsAssemblyDistrict = "";
				String lsCouncilDistrict = "";
				String lsLowEndStreetNumber = "";
				String lsHighEndStreetNumber = "";
				String lsLowEndStreetName = "";
				String lsHighEndStreetName = "";
				if (null != lsBorough && !lsBorough.equalsIgnoreCase(""))
				{
					lsXCoordinate = isNull(loResult.getRootElement().getChildText("XCoordinate"));
					lsYCoordinate = isNull(loResult.getRootElement().getChildText("YCoordinate"));
					lsSchoolDistrictName = isNull(loResult.getRootElement().getChildText("SchoolDistrictName"));
					lsHealthArea = isNull(loResult.getRootElement().getChildText("HealthAreaName"));
					lsBuildingIdNumber = isNull(loResult.getRootElement().getChildText("BuildingID"));
					lsTaxBlock = isNull(loResult.getRootElement().getChildText("TaxBlock"));
					lsTaxLot = isNull(loResult.getRootElement().getChildText("TaxLot"));
					lsSenatorialDistrict = isNull(loResult.getRootElement().getChildText("SenatorialDistrictName"));
					lsAssemblyDistrict = isNull(loResult.getRootElement().getChildText("AssemblyDistrictName"));
					lsCouncilDistrict = isNull(loResult.getRootElement().getChildText("CityCouncilDistrictName"));
					lsLowEndStreetNumber = isNull(loResult.getRootElement().getChildText("LowEndCrossStreetNumber"));
					lsHighEndStreetNumber = isNull(loResult.getRootElement().getChildText("HighEndCrossStreetNumber"));
					lsLowEndStreetName = isNull(loResult.getRootElement().getChildText("LowEndCrossStreetName"));
					lsHighEndStreetName = isNull(loResult.getRootElement().getChildText("HighEndCrossStreetName"));
					lsBorough = isNull(loResult.getRootElement().getChildText("NYCBorough"));
					lsCommunityDistrict = isNull(loResult.getRootElement().getChildText("LocaleCommunityName"));
					lsCivilCourtDistrict = isNull(loResult.getRootElement().getChildText("LocaleJudicialDistrictName"));
				}
				if (null != lsSuggestedStreetName && null != lsSuggestedCityName && null != lsSuggestedState
						&& null != lsSuggestedZipCode)
				{
					if (lsSuggestedStreetName.equalsIgnoreCase(asStreetName)
							&& lsSuggestedCityName.equalsIgnoreCase(asCity)
							&& lsSuggestedState.equalsIgnoreCase(asState)
							&& lsSuggestedZipCode.equalsIgnoreCase(asZipCode))
					{
						abBypassValidation = true;
						setPropertiesInBean(loAddressBean, lsStatusDescription, lsStatusReason, lsStreetNumberText,
								lsCongressionalDistrictName, lsLatitude, lsLongitude, lsXCoordinate, lsYCoordinate,
								lsCommunityDistrict, lsCivilCourtDistrict, lsSchoolDistrictName, lsHealthArea,
								lsBuildingIdNumber, lsTaxBlock, lsTaxLot, lsSenatorialDistrict, lsAssemblyDistrict,
								lsCouncilDistrict, lsLowEndStreetNumber, lsHighEndStreetNumber, lsLowEndStreetName,
								lsHighEndStreetName, lsBorough);
						aoAddressList.add(0, loAddressBean);
						aoRequest.setAttribute("addressList", aoAddressList);
					}
					else
					{
						loSuggestedAddressBean.setMsAddress1(lsSuggestedStreetName);
						loSuggestedAddressBean.setMsCity(lsSuggestedCityName);
						loSuggestedAddressBean.setMsState(lsSuggestedState);
						loSuggestedAddressBean.setMsZipcode(lsSuggestedZipCode);
						loSuggestedAddressBean.setMsAddressType("Suggested Address");
						setPropertiesInBean(loSuggestedAddressBean, lsStatusDescription, lsStatusReason,
								lsStreetNumberText, lsCongressionalDistrictName, lsLatitude, lsLongitude,
								lsXCoordinate, lsYCoordinate, lsCommunityDistrict, lsCivilCourtDistrict,
								lsSchoolDistrictName, lsHealthArea, lsBuildingIdNumber, lsTaxBlock, lsTaxLot,
								lsSenatorialDistrict, lsAssemblyDistrict, lsCouncilDistrict, lsLowEndStreetNumber,
								lsHighEndStreetNumber, lsLowEndStreetName, lsHighEndStreetName, lsBorough);
						/* Begin QC 6527 Release 3.7.0  Suggested address should be defaulted */
						//aoAddressList.add(loSuggestedAddressBean);  ***** COMMENTED ORIGINAL CODE *******
						aoAddressList.add(0, loSuggestedAddressBean);
						/* End QC 6527 Release 3.7.0  Suggested address should be defaulted */
						aoRequest.setAttribute("addressList", aoAddressList);
						aoRequest
								.setAttribute(
										"description",
										"There is a potential problem with the address provided.  A suggested address is listed below.  Please choose which version of the address you want to use and click Select.");
					}
				}
				else
				{
					aoRequest.setAttribute("addressList", aoAddressList);
					aoRequest
							.setAttribute(
									"description",
									"The address could not be verified.  Click Select to choose the address or Cancel to return to the previous screen and edit the address.");
				}
			}
		}
		else
		{
			aoRequest.setAttribute("addressList", aoAddressList);
			aoRequest
					.setAttribute(
							"description",
							"The address could not be verified.  Click Select to choose the address or Cancel to return to the previous screen and edit the address.");
		}
		return abBypassValidation;
	}

	/**
	 * This method will convert non-empty String into empty
	 * @param aoInputStr
	 * @return
	 */
	private String isNull(String aoInputStr)
	{
		String lsReturnVal = "";
		if (aoInputStr != null)
		{
			lsReturnVal = aoInputStr;
		}
		return lsReturnVal;
	}

	/**
	 * This method will set address properties in bean
	 * 
	 * @param aoAddressBean an address bean
	 * @param asStatusDescriptionText a string value of status description text
	 * @param asStatusReason a string value of status reason
	 * @param asStreetNumberText a string value of street number text
	 * @param asCongressionalDistrictName a string value of congressional
	 *            district name
	 * @param asLatitude a string value of latitude
	 * @param asLongitude a string value of longitude
	 * @param asXCoordinate a string value of x-coordinate
	 * @param asYCoordinate a string value of y-coordinate
	 * @param asCommunityDistrict a string value of community district
	 * @param asCivilCourtDistrict a string value of civil court district
	 * @param asSchoolDistrictName a string value of school district
	 * @param asHealthArea a string value of health area
	 * @param asBuildingIdNumber a string value of building Id number
	 * @param asTaxBlock a string value of tax block
	 * @param asTaxLot a string value of tax lot
	 * @param asSenatorialDistrict a string value of senatorial district
	 * @param asAssemblyDistrict a string value of assembly district
	 * @param asCouncilDistrict a string value of council district
	 * @param asLowEndStreetNumber a string value of low end street number
	 * @param asHighEndStreetNumber a string value of high end street number
	 * @param asLowEndStreetName a string value of low end street name
	 * @param asHighEndStreetName a string value of high end street name
	 * @return an address bean object containing address properties
	 */
	private AddressBean setPropertiesInBean(AddressBean aoAddressBean, String asStatusDescriptionText,
			String asStatusReason, String asStreetNumberText, String asCongressionalDistrictName, String asLatitude,
			String asLongitude, String asXCoordinate, String asYCoordinate, String asCommunityDistrict,
			String asCivilCourtDistrict, String asSchoolDistrictName, String asHealthArea, String asBuildingIdNumber,
			String asTaxBlock, String asTaxLot, String asSenatorialDistrict, String asAssemblyDistrict,
			String asCouncilDistrict, String asLowEndStreetNumber, String asHighEndStreetNumber,
			String asLowEndStreetName, String asHighEndStreetName, String asBorough)
	{
		aoAddressBean.setMsStatusDescriptionText(asStatusDescriptionText);
		aoAddressBean.setMsStatusReason(asStatusReason);
		aoAddressBean.setMsStreetNumberText(asStreetNumberText);
		aoAddressBean.setMsCongressionalDistrictName(asCongressionalDistrictName);
		aoAddressBean.setMsLatitude(asLatitude);
		aoAddressBean.setMsLongitude(asLongitude);
		aoAddressBean.setMsXCoordinate(asXCoordinate);
		aoAddressBean.setMsYCoordinate(asYCoordinate);
		aoAddressBean.setMsCommunityDistrict(asCommunityDistrict);
		aoAddressBean.setMsCivilCourtDistrict(asCivilCourtDistrict);
		aoAddressBean.setMsSchoolDistrictName(asSchoolDistrictName);
		aoAddressBean.setMsHealthArea(asHealthArea);
		aoAddressBean.setMsBuildingIdNumber(asBuildingIdNumber);
		aoAddressBean.setMsTaxBlock(asTaxBlock);
		aoAddressBean.setMsTaxLot(asTaxLot);
		aoAddressBean.setMsSenatorialDistrict(asSenatorialDistrict);
		aoAddressBean.setMsAssemblyDistrict(asAssemblyDistrict);
		aoAddressBean.setMsCouncilDistrict(asCouncilDistrict);
		aoAddressBean.setMsLowEndStreetNumber(asLowEndStreetNumber);
		aoAddressBean.setMsHighEndStreetNumber(asHighEndStreetNumber);
		aoAddressBean.setMsLowEndStreetName(asLowEndStreetName);
		aoAddressBean.setMsHighEndStreetName(asHighEndStreetName);
		aoAddressBean.setMsNycBorough(asBorough);

		return aoAddressBean;
	}
}
