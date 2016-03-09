package com.tango.auto.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

/**
 * Assitant Helper:
 * the class will supply static methods for grunt xml node, read txt or csv/xls content.
 * @author tango
 *
 */
public class AssitantHelper extends BaseHelper {
	
	private static String EmptySpace = " ";
	private static String WrapLine = "\r\n";
	
	private static String gruntXmlHeader(String tngListenerClass, String isParallel, String isSingleInstance) {
		StringBuilder sbuilder = new StringBuilder();
		sbuilder.append(String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?>%s", WrapLine));
		sbuilder.append(String.format("<suite name=\"Suite\" parallel=\"%s\">%s", isParallel, WrapLine));
		sbuilder.append(String.format("%4s<listeners>%s", EmptySpace, WrapLine));
		sbuilder.append(String.format("%8s<listener class-name=\"%s\"/>%s", EmptySpace, tngListenerClass, WrapLine));
		sbuilder.append(String.format("%4s</listeners>%s", EmptySpace, WrapLine));
		sbuilder.append(String.format("%4s<test name=\"Test\" group-by-instance=\"%s\">%s", EmptySpace, isSingleInstance, WrapLine));
		sbuilder.append(String.format("%8s<classes>%s", EmptySpace, WrapLine));
		return sbuilder.toString();
	}

	private static String gruntXmlFooter() {
		StringBuilder sbuilder = new StringBuilder();
		sbuilder.append(String.format("%8s</classes>%s", EmptySpace, WrapLine));
		sbuilder.append(String.format("%4s</test>%s", EmptySpace, WrapLine));
		sbuilder.append("</suite>");
		return sbuilder.toString();
	}

	private static String buildXmlNodeFormat(Map<String, List<String>> suiteNodeClassNameMap) {
		StringBuilder sbuilder = new StringBuilder();
		for (String keyFullClassName : suiteNodeClassNameMap.keySet()) {
			List<String> ownMethodNameList = suiteNodeClassNameMap.get(keyFullClassName);
			sbuilder.append(String.format("%12s<class name=\"%s\">\r\n", " ", keyFullClassName));
			sbuilder.append(String.format("%16s<methods>\r\n", " "));
			for (String methodsName : ownMethodNameList) {
				sbuilder.append(String.format("%20s<include name=\"%s\" />\r\n", " ", methodsName));
			}
			sbuilder.append(String.format("%16s</methods>\r\n", EmptySpace));
			sbuilder.append(String.format("%12s</class>\r\n", " "));
		}
		return sbuilder.toString();
	}
	
	public static boolean isWindowOS() {
		try {
			String os = System.getProperty("os.name").trim().toLowerCase();
			if (os.contains(Constants.Env_OS_Platform.toLowerCase())) return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public static void executeActions(boolean isSheet) throws Exception {
		StringBuilder sbuilder = new StringBuilder();
		String header = gruntXmlHeader(
				getConfigKeyValue(Constants.TestNG_Listener), 
				getConfigKeyValue(Constants.Is_Parallel), 
				getConfigKeyValue(Constants.Is_Single_Instance));
		// #.content strings.
		File file = new File(getConfigKeyValue(Constants.Extract_File_Path));
        String xmlContent = "", nodeContent = "";
        if (isSheet) nodeContent = loadCsvFileContent(file);
        else nodeContent = loadTxtFileContent(file);
        String footer = gruntXmlFooter();
        // #.build xml to file.
        xmlContent = sbuilder.append(header).append(nodeContent).append(footer).toString();
        createNewXmlFile(xmlContent);
	}

	private static String loadTxtFileContent(File file) throws Exception {
		InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), Constants.Default_Encoding);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		try {
			String lineCxt = null;
			while ((lineCxt = bufferedReader.readLine()) != null) {
				if (lineCxt.trim().length() > 0) {
					String[] multiParts = lineCxt.trim().split(EmptySpace);
                    if (multiParts.length > 1) {
                        boolean isFullClassName = (multiParts[0].split("[.]").length > 1);
                        String fullClassName = (isFullClassName ? 
                                multiParts[0].trim() : AnalysisHelper.getFullPackageClassName(multiParts[0].trim()));
                        String methodName = multiParts[1].trim();
                        addNodeClassName2Map(fullClassName, methodName);
                    }
                }
            }
			if (bufferedReader != null) bufferedReader.close();
			if (inputStreamReader != null) inputStreamReader.close();
		} catch (Exception ex) {
			bufferedReader.close();
			inputStreamReader.close();
			ex.printStackTrace();
		}
		return AssitantHelper.buildXmlNodeFormat(getSuiteNodeClassNameMap());
	}

	public static String loadCsvFileContent(File file) throws Exception {
	    String[] headNames = {"Package", "Class", "Method (Test Name)", "Result"};
	    String filterStatus = getConfigKeyValue(Constants.Filte_Result_Status);
	    int[] columnIndexs = new int[headNames.length]; 
	    
		Workbook workbook = null;
		WorkbookSettings workbookSettings = new WorkbookSettings();
		workbookSettings.setEncoding(Constants.Default_Encoding);
		workbook = Workbook.getWorkbook(file, workbookSettings);
	    InputStream inputStream = new FileInputStream(file);
	    workbook = Workbook.getWorkbook(inputStream);
	    Sheet sheet = workbook.getSheet(0);
	    
	    Cell[] headerColumns = sheet.getRow(0);
	    for (int i = 0, M = headerColumns.length; i < M; i++) {
            for (int j = 0, N = headNames.length; j<N; j++) {
                if (headerColumns[i].getContents().trim().contains(headNames[j])) columnIndexs[j] = i;
            }
        }
	    int p = columnIndexs[0], c = columnIndexs[1], m = columnIndexs[2], s = columnIndexs[3];
	    for (int r = 1, R = sheet.getRows(); r < R; r++) {
            Cell[] cells = sheet.getRow(r);
            
            String status = cells[s].getContents().trim().toUpperCase();
            if (filterStatus.contains(status) | status.contains(filterStatus)) {
                String fullClassName = cells[p].getContents().trim() + "." + cells[c].getContents().trim();
                String methodName = cells[m].getContents().trim();
                addNodeClassName2Map(fullClassName, methodName);
            }
        }
	    return AssitantHelper.buildXmlNodeFormat(getSuiteNodeClassNameMap());
	}
	
	private static void createNewXmlFile(String xmlContent) throws Exception {
		String outputPath = getConfigKeyValue(Constants.Output_Xml_Path);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-ssss");
		String formatedDtime = simpleDateFormat.format(new Date());
		String filePath = outputPath + Constants.TestNG_File_Name + formatedDtime + Constants.File_Extension_XML;
		File file = new File(filePath);
		if (file.exists()) file.delete();
		file.createNewFile();

		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(xmlContent);
			bufferedWriter.flush();
		} catch (IOException ex) {
			if (bufferedWriter != null) bufferedWriter.close();
			if (fileWriter != null) fileWriter.close();
		}
	}
}
