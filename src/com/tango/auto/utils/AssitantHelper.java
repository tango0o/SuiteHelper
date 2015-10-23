package com.tango.auto.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
	
	public static void executeActions(boolean isSheet) throws Exception {
		StringBuilder sbuilder = new StringBuilder();
		String header = gruntXmlHeader(
				getConfigKeyValueMap().get(Constants.TestNG_Listener), 
				getConfigKeyValueMap().get(Constants.Is_Parallel), 
				getConfigKeyValueMap().get(Constants.Single_Instance));
		// content strings.
		String xmlContent = "", nodeContent = "";
		if (isSheet) return;
		else {
			File file = new File(getConfigKeyValueMap().get(Constants.Extract_File_Path));
			nodeContent = loadTxtFileContent(file);
		}
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
					int index = multiParts[0].lastIndexOf(".");
					lineCxt = multiParts[0].substring(index + 1) + "." + multiParts[1];
					System.out.println(lineCxt);
				}
				String[] splitStrs = lineCxt.split("[.]");
				if (splitStrs.length > 1) {
					AnalysisHelper.searchClasFullName(getConfigKeyValueMap().get(Constants.Base_Dir_Path), 
							splitStrs[0].trim(), splitStrs[1].trim());
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

	private static void createNewXmlFile(String xmlContent) throws Exception {
		String outputPath = getConfigKeyValueMap().get(Constants.Output_Xml_Path);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-ss");
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
