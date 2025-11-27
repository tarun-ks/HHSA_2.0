package com.nyc.hhs.daomanager.serviceTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class UnitTestCasesMethodCount
{
	// static String path =
	// "C:\\Eclipse_workspace\\HHSPortal\\r2_test\\com\\nyc\\hhs\\daomanager\\serviceTest\\ProposalServiceTest.java";
	static String path = "C:\\Eclipse_workspace\\HHSPortal\\r2_test\\";
	static String methodToSearch = "cancelProposal,retractProposal,insertProposalDocumentDetails";
	static Map<String, Integer> testCasesCount = new LinkedHashMap<String, Integer>();
	static List cellDataList = new ArrayList();
	static String excelPath = "C:\\Users\\deepak.singh.kirar\\Desktop\\ACQT\\ACQT_Summary.xls";

	private static void methodTestCasesCount(String fullClassName, String className, String methodName, int cellNumber)
			throws IOException
	{
		BufferedReader bufferedReader = null;
		try
		{
			int linecount = 0;
			String line;
			// Let the user know what we are searching for
			// Loop through each line, stashing the line into our line variable.
			// List<String> methodList = new
			// ArrayList<String>(Arrays.asList(methodToSearch.split(",")));
			// for (String methodName : methodList)
			// {
			bufferedReader = new BufferedReader(new FileReader(fullClassName));
			Integer counter = 0;
			while ((line = bufferedReader.readLine()) != null)
			{
				// Increment the count and find the index of the word
				linecount++;
				// int indexfound = line.indexOf(stringSearch);
				// Boolean wordFound =
				// line.matches(".*\\b".concat(methodName).concat("\\b.*"));
				 Boolean wordFound = line.matches(".*\\b".concat(methodName).concat("\\b.*"));
				// If greater than -1, means we found the word
				if (wordFound)
				{
					/*System.out.println(methodName.concat(" method was found  on line ") + linecount
							+ " in class ".concat(className));*/
					counter = counter + 1;
					if (testCasesCount.containsKey(methodName))
					{
						testCasesCount.remove(methodName);
						testCasesCount.put(methodName, counter);
						// System.out.println("===========>>>============="+testCasesCount);
					}
					else
					{
						testCasesCount.put(methodName, counter);
					}
					FileInputStream file = new FileInputStream(new File(excelPath));
					HSSFWorkbook wb = new HSSFWorkbook(file);
					HSSFSheet st = wb.getSheetAt(3);
					HSSFRow row = st.createRow((short) cellNumber);
					HSSFCell cell = row.createCell((short) 3);

					cell.setCellType(cell.CELL_TYPE_STRING);
					cell.setCellValue(counter);
					FileOutputStream fileOut = new FileOutputStream(excelPath);
					wb.write(fileOut);
					fileOut.close();
				}
			}
			// System.out.println("=====Test cases count for the " +
			// methodToSearch + " is : " + counter);
			// }
			// Close the file after done searching
			bufferedReader.close();
		}
		catch (IOException e)
		{
			//System.out.println("IO Error Occurred: " + e.toString());
		}
	}

	private static void readExcel(String fileName)
	{
		/**
		 * Create a new instance for cellDataList
		 */
		try
		{
			/**
			 * Create a new instance for FileInputStream class
			 */
			FileInputStream fileInputStream = new FileInputStream(fileName);
			/**
			 * Create a new instance for POIFSFileSystem class
			 */
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);
			/*
			 * Create a new instance for HSSFWorkBook Class
			 */
			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(3);
			/**
			 * Iterate the rows and cells of the spreadsheet to get all the
			 * datas.
			 */
			Iterator rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext())
			{
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator iterator = hssfRow.cellIterator();
				List cellTempList = new ArrayList();
				Map createMap = new LinkedHashMap();
				while (iterator.hasNext())
				{
					HSSFCell hssfCell = (HSSFCell) iterator.next();
					cellTempList.add(hssfCell);
					createMap.put(hssfRow, cellTempList);
				}
				// cellDataList.add(cellTempList);
				cellDataList.add(createMap);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		/**
		 * Call the printToConsole method to print the cell data in the console.
		 */
		// printToConsole(cellDataList);
	}

	/**
	 * This method is used to print the cell data to the console.
	 * @param cellDataList - List of the data's in the spreadsheet.
	 * @throws IOException
	 */
	private static void printToConsole(List cellDataList, String fullPath, String className) throws IOException
	{
		for (int i = 0; i < cellDataList.size(); i++)
		{
			// List cellTempList = (List) cellDataList.get(i);
			Map tempMap = (Map) cellDataList.get(i);
			Iterator it = tempMap.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry pairs = (Map.Entry) it.next();
				HSSFRow hssfRow = (HSSFRow) pairs.getKey();
				List cellTempList = (List) pairs.getValue();

				for (int j = 0; j < cellTempList.size(); j++)
				{
					HSSFCell hssfCell = (HSSFCell) cellTempList.get(j);
					if (hssfCell.getCellType() == 0)
					{
						System.out.print(hssfCell.getNumericCellValue() + "\t");
					}
					else
					{
						if (j == 1)
						{
							if (hssfCell.getStringCellValue().lastIndexOf(".") > 0
									&& hssfCell.getStringCellValue().lastIndexOf("(") > 0)
							{
								String methodName = hssfCell.getStringCellValue().substring(
										hssfCell.getStringCellValue().lastIndexOf(".")+1,
										hssfCell.getStringCellValue().lastIndexOf("("));
								methodTestCasesCount(fullPath, className, methodName, hssfRow.getRowNum());
							}
						}
						System.out.print(hssfCell.getStringCellValue() + "\t");
					}
				}
			}
			//System.out.println("\n");
		}
	}

	/**
	 * Method starting point
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String args[]) throws IOException, ClassNotFoundException
	{
		FileInputStream file = new FileInputStream(new File(excelPath));
		HSSFWorkbook wb = new HSSFWorkbook(file);
		HSSFSheet st = wb.getSheetAt(3);
		HSSFRow row = st.createRow((short) 5);
		HSSFCell cell = row.createCell((short) 3);
		cell.setCellType(cell.CELL_TYPE_STRING);
		cell.setCellValue("Junits Written");
		FileOutputStream fileOut = new FileOutputStream(excelPath);
		wb.write(fileOut);
		fileOut.close();

		// method to read the excel
		readExcel(excelPath);

		String lsPackageName = null;
		Integer loTotalClassInPackage = 0;
		Integer loTotalMethods = 0;

		lsPackageName = "com.nyc.hhs.daomanager.serviceTest";
		List<String> loClassNamesList = getClassNamesFromPackage(lsPackageName);
		loTotalMethods = getMethodCount(loClassNamesList, lsPackageName, loTotalClassInPackage, loTotalMethods);

		lsPackageName = "com.nyc.hhs.handlersTest";
		loClassNamesList = getClassNamesFromPackage(lsPackageName);
		loTotalMethods = getMethodCount(loClassNamesList, lsPackageName, loTotalClassInPackage, loTotalMethods);

		lsPackageName = "com.nyc.hhs.handlersTest.ruleresulthandlersTest";
		loClassNamesList = getClassNamesFromPackage(lsPackageName);
		loTotalMethods = getMethodCount(loClassNamesList, lsPackageName, loTotalClassInPackage, loTotalMethods);

		lsPackageName = "com.nyc.hhs.ruleTest";
		loClassNamesList = getClassNamesFromPackage(lsPackageName);
		loTotalMethods = getMethodCount(loClassNamesList, lsPackageName, loTotalClassInPackage, loTotalMethods);

		/*System.out.println("==Total method count===" + loTotalMethods);
		System.out.println("==junit method and their count ===" + testCasesCount);*/
	}

	/**
	 * Method count inside the package
	 * @param asClassNamesList
	 * @param asPackageName
	 * @param aoTotalClassInPackage
	 * @param aoTotalMethods
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Integer getMethodCount(List<String> asClassNamesList, String asPackageName,
			Integer aoTotalClassInPackage, Integer aoTotalMethods) throws ClassNotFoundException, IOException
	{
		Integer loTotalMethodsInPackage = 0;
		for (String className : asClassNamesList)
		{
			if (className != null && !className.equalsIgnoreCase("UnitTestCasesMethodCount"))
			{
				Class loClassName = Class.forName(asPackageName + "." + className);
				loTotalMethodsInPackage = loTotalMethodsInPackage + loClassName.getMethods().length;
				aoTotalMethods = aoTotalMethods + loClassName.getMethods().length;
				++aoTotalClassInPackage;
				String fullPath = path.concat((asPackageName + "\\" + className).replace(".", "\\").concat(".java"));
				printToConsole(cellDataList, fullPath, className);
			}
		}
		/*System.out.println("=====lsPackageName=>" + asPackageName + "<===loTotalClassInPackage=>"
				+ aoTotalClassInPackage + "<===loTotalMethodInPackage=>" + loTotalMethodsInPackage);*/
		return aoTotalMethods;
	}

	/**
	 * This method is used to get the list of class name inside the package
	 * @param asPackageName
	 * @return
	 * @throws IOException
	 */
	public static List<String> getClassNamesFromPackage(String asPackageName) throws IOException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL packageURL;
		List<String> names = new ArrayList<String>();
		asPackageName = asPackageName.replace(".", "/");
		packageURL = classLoader.getResource(asPackageName);

		if (packageURL.getProtocol().equals("jar"))
		{
			String jarFileName;
			JarFile jf;
			Enumeration<JarEntry> jarEntries;
			String entryName;

			// build jar file name, then loop through zipped entries
			jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
			jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
			//System.out.println(">" + jarFileName);
			jf = new JarFile(jarFileName);
			jarEntries = jf.entries();
			while (jarEntries.hasMoreElements())
			{
				entryName = jarEntries.nextElement().getName();
				if (entryName.startsWith(asPackageName) && entryName.length() > asPackageName.length() + 5)
				{
					entryName = entryName.substring(asPackageName.length(), entryName.lastIndexOf('.'));
					names.add(entryName);
				}
			}
			// loop through files in classpath
		}
		else
		{
			File folder = new File(packageURL.getFile());
			File[] contenuti = folder.listFiles();
			String entryName;
			for (File actual : contenuti)
			{
				entryName = actual.getName();
				if (entryName.contains("."))
				{
					entryName = entryName.substring(0, entryName.lastIndexOf('.'));
					names.add(entryName);
				}
			}
		}
		return names;
	}
}